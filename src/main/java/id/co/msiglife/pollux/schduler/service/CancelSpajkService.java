package id.co.msiglife.pollux.schduler.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import id.co.msiglife.pollux.schduler.dto.ApiResponseDTO;
import id.co.msiglife.pollux.schduler.dto.CancelSpajkDTO;
import id.co.msiglife.pollux.schduler.entity.*;
import id.co.msiglife.pollux.schduler.repository.*;
import id.co.msiglife.pollux.schduler.util.ConvertUtil;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static id.co.msiglife.pollux.schduler.util.ConvertUtil.stringBlank;
import static id.co.msiglife.pollux.schduler.util.StaticValue.*;

@Service
public class CancelSpajkService {

    @Autowired
    MasterPolicyRepository masterPolicyRepository;

    @Autowired
    MasterInsuredRepository masterInsuredRepository;

    @Autowired
    MasterWFDocumentRepository masterWFDocumentRepository;

    @Autowired
    MappingBcaRepository mappingBcaRepository;

    @Autowired
    ListWFConfigRepository listWFConfigRepository;

    @Autowired
    MasterWFDocumentHistoryRepository masterWFDocumentHistoryRepository;

    @Autowired
    MasterUnderwritingRepository masterUnderwritingRepository;

    @Autowired
    ListWFStatusRepository listWFStatusRepository;

    @Autowired
    MasterPositionSPAJRepository masterPositionSPAJRepository;

    @Autowired
    MasterWfRequestRepository masterWFRequestRepository;

    @Autowired
    MasterWfLogActivityPolluxRepository masterWfLogActivityPolluxRepository;

    @Autowired
    ListNegaraRepository listNegaraRepository;

    @Autowired
    MasterProductInsuredRepository masterProductInsuredRepository;

    @Autowired
    MasterAddressNewRepository masterAddressNewRepository;

    @Autowired
    MasterClientRepository masterClientRepository;

    @Autowired
    MasterPersonRepository masterPersonRepository;

    @Autowired
    MasterUploadDokumenRepository masterUploadDokumenRepository;

    @Autowired
    ListPromotionPackageRepository listPromotionPackageRepository;

    @Value("${spring.custom.storage.path}")
    private String STORAGE_PATH;

    @Scheduled(cron = "0 40 1 * * ?")
    public void cancelSpajkScheduler(){
        List<CancelSpajkDTO> cancelSpajkDTOList = getCancelSpajkList();
        System.out.println("=================== START EXECUTING TASK: CANCEL SPAJK (" + cancelSpajkDTOList.size() + ") Data ===================");
        this.executeTask(cancelSpajkDTOList);
        System.out.println("=================== FINISHED EXECUTING TASK: CANCEL SPAJK ===================");
    }

    @Scheduled(cron = "0 40 5 * * ?")
    public void retryCancelSpajkScheduler(){
        List<CancelSpajkDTO> cancelSpajkDTOList = getCancelSpajkList();
        System.out.println("=================== RETRY EXECUTING TASK: CANCEL SPAJK (" + cancelSpajkDTOList.size() + ") Data ===================");
        this.executeTask(cancelSpajkDTOList);
        System.out.println("=================== FINISHED RETRYING EXECUTING TASK: CANCEL SPAJK ===================");
    }

    private List<CancelSpajkDTO> getCancelSpajkList() {
        List<Object[]> resultList = masterPolicyRepository.findPolicyTobeCancelled();
        List<CancelSpajkDTO> policyToBeCancelledList = new ArrayList<>();
        for (Object[] row : resultList) {
            CancelSpajkDTO dto = new CancelSpajkDTO();
            dto.setRegSpaj(ConvertUtil.safeString(row[0]));
            policyToBeCancelledList.add(dto);
        }
        return policyToBeCancelledList;
    }

    private void executeTask(List<CancelSpajkDTO> cancelSpajkDTOList){

        Map<String, Object> result = new HashMap<>();
        MasterWfLogActivityPollux wfLog = new MasterWfLogActivityPollux();
        wfLog.setIpAddress("127.0.0.1");
        wfLog.setEndpointUrl("/cancel-data-peserta");
        wfLog.setMethod("PUT");
        wfLog.setUserId(0);

        try {
            int errorIn = 3;
            int iteration = 0;

            //get list all spajk to be cancelled
            MasterPositionSPAJ masterPositionSPAJ = new MasterPositionSPAJ();

            for (CancelSpajkDTO cancelDto : cancelSpajkDTOList) {
                try{
//                    System.out.println("iteration ke-" + iteration);
//                    if(iteration == errorIn){
//                        iteration++;
//                        throw new RuntimeException("Coba break");
//                    }
//                    iteration++;

                    MasterPolicy existingPolicy = masterPolicyRepository.findByRegSpaj(cancelDto.getRegSpaj());
                    MasterInsured existingInsured = masterInsuredRepository.findByRegSpajAndInsuredNo(cancelDto.getRegSpaj(), 1);
                    MasterWFDocument masterWFDocument = masterWFDocumentRepository.findMasterWFDocumentByDocNumberAndLswfTypeId(cancelDto.getRegSpaj(), 78);

                    // check status cancel apakah sudah pernah diproses
                    Optional<BcaData> bcaDataOrder = mappingBcaRepository.findByRegSpajAndTipeTrans(cancelDto.getRegSpaj(), 3);

                    if (masterWFDocument == null) {
//                return new ApiResponseDTO<>(400, "SPAJ " + existingPolicy.getRegSpaj() + " Document not found.", result);
                        throw new RuntimeException("SPAJ " + cancelDto.getRegSpaj() + " Document not found.");
                    }else {
                        ListWFConfig wfConfig = listWFConfigRepository.findListWFConfigByTypeIdAndStateIdFromAndStateIdTo(masterWFDocument.getLswfTypeId(), masterWFDocument.getLswfStateId(), -4);
                        Optional<MasterWFDocumentHistory> masterWFDocumentHistoryList = masterWFDocumentHistoryRepository.findFirstByDocIdAndLswfTypeIdAndDocNumberOrderByDocHistIdDesc(masterWFDocument.getDocId(), masterWFDocument.getLswfTypeId(), masterWFDocument.getDocNumber());
                        MasterUnderwriting existMasterUnderwriting = masterUnderwritingRepository.findbyRegSpajk(masterWFDocument.getDocNumber());
                        if(existMasterUnderwriting != null) {
                            ListWFStatus listWFStatus = listWFStatusRepository.findListWFStatusByTypeIdAndStatusId(masterWFDocument.getLswfTypeId(), masterWFDocument.getLswfStatusId());
                            if (existMasterUnderwriting.getFinalDecision().toUpperCase().equals("POSTPONE") || listWFStatus.getStatusId() == STATUS_WORKSHEET_POSTPONED || existMasterUnderwriting.getFinalDecision().toUpperCase().equals("DECLINE") || listWFStatus.getStatusId() == STATUS_WORKSHEET_DECLINED || listWFStatus.getStatusId() == STATUS_WORKSHEET_INFORCED || existMasterUnderwriting.getFinalDecision().toUpperCase().equals("CANCEL")) {
                                if (masterWFDocumentHistoryList.isPresent()) {
                                    MasterWFDocumentHistory masterWFDocumentHistory = masterWFDocumentHistoryList.get();
                                    if (masterWFDocumentHistory.getLswfStateIdTo() != STATUS_WORKSHEET_APPROVED && (listWFStatus.getStatusId() != STATUS_WORKSHEET_POSTPONED && listWFStatus.getStatusId() != STATUS_WORKSHEET_DECLINED)) {
//                                    return new ApiResponseDTO<>(400, "SPAJ is still pending approval. Please complete the approval process before proceeding with the cancellation.", result);
                                        throw new RuntimeException("SPAJ is still pending approval. Please complete the approval process before proceeding with the cancellation.");
                                    } else {
                                        if (existMasterUnderwriting.getFinalDecision().toUpperCase().equals("DECLINE") || listWFStatus.getStatusId() == STATUS_WORKSHEET_DECLINED) {
//                                        return new ApiResponseDTO<>(400, "SPAJ with Decline status cannot be canceled.", result);
                                            throw new RuntimeException("SPAJ with Decline status cannot be canceled.");
                                        } else if (listWFStatus.getStatusId() == STATUS_WORKSHEET_INFORCED) {
//                                        return new ApiResponseDTO<>(400, "SPAJ with Inforce status cannot be canceled.", result);
                                            throw new RuntimeException("SPAJ with Inforce status cannot be canceled.");
                                        } else if (existMasterUnderwriting.getFinalDecision().toUpperCase().equals("CANCEL")) {
//                                        return new ApiResponseDTO<>(400, "SPAJ with Cancel status cannot be canceled.", result);
                                            throw new RuntimeException("SPAJ with Cancel status cannot be canceled.");
                                        } else if (existMasterUnderwriting.getFinalDecision().toUpperCase().equals("POSTPONE") || listWFStatus.getStatusId() == STATUS_WORKSHEET_POSTPONED) {
//                                        return new ApiResponseDTO<>(400, "SPAJ with Postpone status cannot be canceled.", result);
                                            throw new RuntimeException("SPAJ with Postpone status cannot be canceled.");
                                        }
                                    }
                                }
                            }
                        }

                        //handling spaj bca start
                        if(bcaDataOrder.isPresent()){
                            BcaData bcaRevisionData = bcaDataOrder.get();
                            // update status ORDER di data order api
                            bcaRevisionData.setOrderStatus(1);
                            bcaRevisionData.setOrderProcessDate(new Date());
//                        bcaRevisionData.setOrderUserProcess(auth.getUserId());
                            bcaRevisionData.setOrderUserProcess(0);
                            System.out.println("Update cancel order data BCA Reg SPAJ: " + cancelDto.getRegSpaj());
                            mappingBcaRepository.save(bcaRevisionData);
                            System.out.println("Order data updated successfully...");

                            BcaData existingBcaOrderData = mappingBcaRepository.findByRegSpajAndTipeTrans(cancelDto.getRegSpaj(), 1).get();
                            existingBcaOrderData.setOrderStatus(2);
                            existingBcaOrderData.setOrderProcessDate(new Date());
                            existingBcaOrderData.setOrderUserProcess(0);
                            System.out.println("Update existing order data BCA Reg SPAJ: " + cancelDto.getRegSpaj());
                            mappingBcaRepository.save(existingBcaOrderData);
                            System.out.println("Order data updated successfully...");

                            ApiResponseDTO<Map<String, Object>> generatePdfBcaResponse = handlingPdfCancelBca(cancelDto.getRegSpaj(), 3, bcaDataOrder, existingPolicy, existingInsured);
                            if (generatePdfBcaResponse.getCode() != 200) {
                                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                throw new RuntimeException(generatePdfBcaResponse.getMessage());
                            }

                        }
                        //handling spaj bca end

                        existingPolicy.setLsspId(12);
                        existingPolicy.setLspdId(999);
                        existingPolicy.setNote("CANCELLED AUTOMATICALLY VIA SCHEDULER; EXCEEDED 61 DAYS");
                        existingInsured.setTglBatal(new Date());

                        masterPositionSPAJ.setRegSpaj(cancelDto.getRegSpaj());
                        masterPositionSPAJ.setPositionDate(existingInsured.getTglBatal());
                        masterPositionSPAJ.setLsspId(existingPolicy.getLsspId());
                        masterPositionSPAJ.setLspdId(existingPolicy.getLspdId());
//                    masterPositionSPAJ.setLusId(existingPolicy.getLusId());
                        masterPositionSPAJ.setLusId(0);
                        masterPositionSPAJ.setDesc(existingPolicy.getNote());
                        masterPositionSPAJ.setLssaId(existingInsured.getLssaId());

                        masterPolicyRepository.save(existingPolicy);
                        masterInsuredRepository.save(existingInsured);
                        masterPositionSPAJRepository.save(masterPositionSPAJ);

                        masterWFDocument.setLswfStateId(wfConfig.getStateIdTo());
                        masterWFDocument.setLswfStatusId(11);
                        this.insertMasterWFDocumentHistory(
                                masterWFDocument.getDocId(),
                                masterWFDocument.getLswfTypeId(),
                                masterWFDocument.getParentDocId(),
                                wfConfig.getStateIdFrom(),
                                wfConfig.getConfigAction(),
                                //auth.getUserId(),
                                0,
                                wfConfig.getStateIdTo(),
                                new Date(),
                                null,
                                null,
                                null,
                                masterWFDocument.getDocNumber(),
                                wfConfig.getStatusId()
                        );

                        MasterWfRequest masterWfRequest = this.insertMasterWfRequest(
                                masterWFDocument.getDocId(),
                                wfConfig.getConfigId(),
                                wfConfig.getConfigAction(),
                                masterWFDocument.getLswfTypeId(),
                                wfConfig.getStateIdFrom(),
                                new ObjectMapper().writeValueAsString(masterWFDocument),
                                new Date()
                        );

                        masterWFDocumentRepository.save(masterWFDocument);

                    }
                    wfLog.setDocNumber(cancelDto.getRegSpaj());
                    wfLog.setSuccess(true);
                    wfLog.setResponse(new ObjectMapper().writeValueAsString(result));
                    wfLog.setLogDate(new Date());
                    masterWfLogActivityPolluxRepository.save(wfLog);

                } catch (Exception e) {
                    System.err.println("Error when cancelling SPAJK");
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println("Error log: ");
            result.put("error", e.getMessage());
            String message = !e.getMessage().isEmpty() ? e.getMessage() : "Error while updating document workflow";
            e.printStackTrace();
        }

    }

    private MasterWFDocumentHistory insertMasterWFDocumentHistory(Long docId, Integer lswfTypeId, Long parentDocId, Integer lswfStateIdFrom, String lswfConfigAction, Integer lusId, Integer lswfStateIdTo, Date docHistCreated, String docStatusKey, Integer docStatusValue, String docHistRemarks, String docNumber, Integer lswfStatusId) {
        MasterWFDocumentHistory masterWFDocumentHistory = new MasterWFDocumentHistory();
        masterWFDocumentHistory.setDocId(docId);
        masterWFDocumentHistory.setLswfTypeId(lswfTypeId);
        masterWFDocumentHistory.setParentDocId(parentDocId);
        masterWFDocumentHistory.setLswfStateIdFrom(lswfStateIdFrom);
        masterWFDocumentHistory.setLswfConfigAction(lswfConfigAction);
        masterWFDocumentHistory.setLusId(lusId);
        masterWFDocumentHistory.setLswfStateIdTo(lswfStateIdTo);
        masterWFDocumentHistory.setDocHistCreated(docHistCreated);
        masterWFDocumentHistory.setDocStatusKey(docStatusKey);
        masterWFDocumentHistory.setDocStatusValue(docStatusValue);
        masterWFDocumentHistory.setDocHistRemarks(docHistRemarks);
        masterWFDocumentHistory.setDocNumber(docNumber);
        masterWFDocumentHistory.setLswfStatusId(lswfStatusId);
        return masterWFDocumentHistoryRepository.save(masterWFDocumentHistory);
    }

    private MasterWfRequest insertMasterWfRequest(Long docId, Integer lswfConfigId, String lswfConfigAction, Integer lswfTypeId, Integer lswfStateIdFrom, String requestDet, Date requestCreatedDate) {
        MasterWfRequest masterWfRequest = new MasterWfRequest();
        masterWfRequest.setParentDocId(docId);
        masterWfRequest.setDocId(docId);
        masterWfRequest.setLswfConfigId(lswfConfigId);
        masterWfRequest.setLswfConfigAction(lswfConfigAction);
        masterWfRequest.setLswfTypeId(lswfTypeId);
        masterWfRequest.setLswfStateIdFrom(lswfStateIdFrom);
        masterWfRequest.setRequestDet(requestDet);
        masterWfRequest.setRequestCreatedDate(requestCreatedDate);
        return masterWFRequestRepository.save(masterWfRequest);
    }

    public ApiResponseDTO<Map<String, Object>> handlingPdfCancelBca(String regSpaj, Integer tipeTransaksi, Optional<BcaData> bcaOrderData, MasterPolicy policy, MasterInsured insured) {
        Map<String, Object> result = new HashMap<>();

        try {

            if(policy == null){
                throw new RuntimeException("Data Policy not found");
            }

            if (insured == null) {
                throw new RuntimeException("Insured not found");
            }

            MasterPerson person = masterPersonRepository.findByMclId(insured.getInsured());
            MasterClient client = masterClientRepository.findByMclId(insured.getInsured());
            MasterClient clientCabang = masterClientRepository.findByMclId(policy.getCustomer());
            MasterAddressNew addressNew = masterAddressNewRepository.findByMclId(insured.getInsured());
            MasterProductInsured productInsured = masterProductInsuredRepository.findMainProduct(insured.getRegSpaj(), 0, 1).get(0);
            ListNegara negara = listNegaraRepository.findByLsneId(person.getLsneId());

            BcaData tempBcaData = new BcaData();
            tempBcaData.setOrderId(bcaOrderData.get().getOrderId());
            tempBcaData.setRegSpaj(bcaOrderData.get().getRegSpaj());
            tempBcaData.setReferenceNo(bcaOrderData.get().getReferenceNo());
            tempBcaData.setNamaCabang(clientCabang.getFirst());
            tempBcaData.setNomorIdTertanggung(person.getNoIdentity());
            tempBcaData.setKewarganegaraan(negara == null ? "" : negara.getNote().equalsIgnoreCase("INDONESIA") ? "WNI" : "WNA");
            tempBcaData.setNamaLengkapTertanggung(client.getFirst());
            tempBcaData.setJenisKelamin(person.getSex() == 0 ? "F" : "M");
            tempBcaData.setPekerjaan(person.getKerja());
            tempBcaData.setCompanyName(bcaOrderData.get().getCompanyNameOther());
            tempBcaData.setTeleponRumah(addressNew.getTelponRumah());
            tempBcaData.setTeleponKantor(bcaOrderData.get().getTeleponKantor());
            tempBcaData.setHandphone(person.getNoHp());
            tempBcaData.setEmail(person.getEmail());
            tempBcaData.setTempatLahir(person.getPlaceBirth());
            tempBcaData.setTanggalLahir(new SimpleDateFormat("dd/MM/yyyy").format(person.getDateBirth().getTime()));
            tempBcaData.setMasaPertanggungan((policy.getInsPeriod()*12) + (policy.getInsBulan() != null ? policy.getInsBulan() : 0));
            tempBcaData.setNilaiPertanggungan(productInsured.getTsi().doubleValue());
            tempBcaData.setKeterangan(bcaOrderData.get().getKeterangan());
            tempBcaData.setTglInsertApi(bcaOrderData.get().getTglInsertApi());
            tempBcaData.setPicBca(bcaOrderData.get().getPicBca());
            tempBcaData.setPicOrder(policy.getNoKerjasama());
            tempBcaData.setNamaFileCovernote(bcaOrderData.get().getNamaFileCovernote());

            ListPromotionPackage listPromotionPackage = listPromotionPackageRepository.findByReferenceNoFirstOnly(bcaOrderData.get().getReferenceNo());
            String promotionPackage = listPromotionPackage == null ? "" : listPromotionPackage.getPromotionDesc();
            String printDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());

            BigDecimal up = tempBcaData.getNilaiPertanggungan() == null ? BigDecimal.ZERO : BigDecimal.valueOf(tempBcaData.getNilaiPertanggungan());
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            String dob = tempBcaData.getTanggalLahir();
            String pob = stringBlank(tempBcaData.getTempatLahir()) ? "-" : tempBcaData.getTempatLahir();
            String inputRevisionDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(tempBcaData.getTglInsertApi());

            // read template file
            String filePath = "template/bca_template.html";
            String content;

            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath)) {
                assert inputStream != null;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                    StringBuilder contentBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        contentBuilder.append(line).append(System.lineSeparator());
                    }
                    content = contentBuilder.toString();
                }
            }

            // replace content from template file here
            content = content.replace("{promotion_package}", stringBlank(promotionPackage) ? "" : promotionPackage);
            content = content.replace("{tipe_transaksi_code}", "3");
            content = content.replace("{tipe_transaksi}", "Cancellation");
            content = content.replace("{order_id}", stringBlank(tempBcaData.getOrderId()) ? "-" : tempBcaData.getOrderId());
            content = content.replace("{reg_spaj}", stringBlank(tempBcaData.getRegSpaj()) ? "-" : tempBcaData.getRegSpaj());
            content = content.replace("{reference_number}", stringBlank(tempBcaData.getReferenceNo()) ? "-" : tempBcaData.getReferenceNo());
            content = content.replace("{nama_cabang}", stringBlank(tempBcaData.getNamaCabang()) ? "-" : tempBcaData.getNamaCabang());
            content = content.replace("{id_number_tertanggung}", stringBlank(tempBcaData.getNomorIdTertanggung()) ? "-" : tempBcaData.getNomorIdTertanggung());
            content = content.replace("{kewarganegaraan}", stringBlank(tempBcaData.getKewarganegaraan()) ? "-" : tempBcaData.getKewarganegaraan());
            content = content.replace("{fullname_tertanggung}", stringBlank(tempBcaData.getNamaLengkapTertanggung()) ? "-" : tempBcaData.getNamaLengkapTertanggung());
            content = content.replace("{gender}", stringBlank(tempBcaData.getJenisKelamin()) ? "-" : (tempBcaData.getJenisKelamin().equalsIgnoreCase("F") ? "Female" : "Male"));
            content = content.replace("{pekerjaan}", stringBlank(tempBcaData.getPekerjaan()) ? "-" : tempBcaData.getPekerjaan());
            content = content.replace("{company_name_other}", stringBlank(tempBcaData.getCompanyNameOther()) ? "-" : tempBcaData.getCompanyNameOther());
            content = content.replace("{home_phone_number}", stringBlank(tempBcaData.getTeleponRumah()) ? "-" : tempBcaData.getTeleponRumah());
            content = content.replace("{office_phone_number}", stringBlank(tempBcaData.getTeleponKantor()) ? "-" : tempBcaData.getTeleponKantor());
            content = content.replace("{mobile_number}", stringBlank(tempBcaData.getHandphone()) ? "-" : tempBcaData.getHandphone());
            content = content.replace("{email}", stringBlank(tempBcaData.getEmail()) ? "-" : tempBcaData.getEmail());
            content = content.replace("{pob_dob}", (dob.equals("-") || pob.equals("-")) ? "-" : pob + ", " + dob);
            content = content.replace("{masa_pertanggungan}", tempBcaData.getMasaPertanggungan() == null ? "-" : tempBcaData.getMasaPertanggungan().toString());
            content = content.replace("{nilai_pertanggungan}", currencyFormat.format(up));
            content = content.replace("{keterangan}", stringBlank(tempBcaData.getKeterangan()) ? "-" : tempBcaData.getKeterangan());
            content = content.replace("{input_date}", inputRevisionDate);
            content = content.replace("{print_date}", printDate);
            content = content.replace("{pic_bca}", stringBlank(tempBcaData.getPicBca()) ? "-" : tempBcaData.getPicBca());
            content = content.replace("{pic_order}", stringBlank(tempBcaData.getPicBca()) ? "-" : tempBcaData.getPicOrder());
            content = content.replace("{covernote_file_name}", stringBlank(tempBcaData.getNamaFileCovernote()) ? "-" : tempBcaData.getNamaFileCovernote());

            System.out.println("Printed HTML content: \n" + content);

            // create string HTML to a JSoup Document to render XHTML
            Document document = Jsoup.parse(content);
            document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);

            // check if folder hasn't existed
            String folderPath = STORAGE_PATH + tempBcaData.getRegSpaj();

            File folder = new File(folderPath);
            File parentDirectory = folder.getParentFile();
            if (parentDirectory.canWrite()) {
                if (!folder.exists()) {
                    folder.mkdirs();
                }
            } else {
                throw new IOException("No write permissions in the parent directory: " + parentDirectory);
            }

            // PdfRendererBuilder will take this XHTML document and create a PDF as the output file
            String outputPdf = folderPath + "/" + tempBcaData.getRegSpaj() + "_BCA_Cancellation_" + bcaOrderData.get().getNoUrut() + ".pdf";

            try (OutputStream os = Files.newOutputStream(Paths.get(outputPdf))) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.withUri(outputPdf);
                builder.toStream(os);
                builder.withW3cDocument(new W3CDom().fromJsoup(document), folderPath + "/");
                builder.run();
            }

            // insert into uploaded file list
            List<MasterUploadDokumen> uploadDokumenList = masterUploadDokumenRepository.findByRegSpajAndJenisDokumenId(regSpaj, 10); // find worksheet docs
            MasterUploadDokumen uploadPesertaBca = new MasterUploadDokumen();

            // check last id
            Integer lastDokumenId = masterUploadDokumenRepository.findLastDokumenIdByRegSpaj(regSpaj);
            lastDokumenId = lastDokumenId != null ? lastDokumenId : 0;

            if (uploadDokumenList.isEmpty()) {
                uploadPesertaBca = new MasterUploadDokumen();
                uploadPesertaBca.setRegSpaj(regSpaj);
                uploadPesertaBca.setDokumenId(lastDokumenId + 1);
                uploadPesertaBca.setJenisDokumenId(10);
                uploadPesertaBca.setUploadCreatedDate(new Date());
            } else {
                boolean isTipeMatch = false;
                MasterUploadDokumen temp = new MasterUploadDokumen();
                //looping untuk menemukan dokumen yang match berdasarkan tipe transaksi + nomor urut
                for(MasterUploadDokumen uploadDokumen : uploadDokumenList){
                    if(uploadDokumen.getPathFile().equalsIgnoreCase(outputPdf)){
                        isTipeMatch = true;
                        temp = uploadDokumen;
                        break;
                    }
                }
                if(isTipeMatch){
                    uploadPesertaBca = temp;
                    uploadPesertaBca.setUploadUpdatedDate(new Date());
                }else{
                    uploadPesertaBca = new MasterUploadDokumen();
                    uploadPesertaBca.setRegSpaj(regSpaj);
                    uploadPesertaBca.setDokumenId(lastDokumenId + 1);
                    uploadPesertaBca.setJenisDokumenId(10);
                    uploadPesertaBca.setUploadCreatedDate(new Date());
                }
            }

//            uploadPesertaBca.setLusId(authJwt.getUserId());
            uploadPesertaBca.setLusId(0);
            uploadPesertaBca.setPathFile(outputPdf);
            masterUploadDokumenRepository.save(uploadPesertaBca);

            return new ApiResponseDTO<>(200, "PDF Cancel BCA created and uploaded successfully.", result);
        } catch (Exception e) {
            System.out.println("Error log: ");
            result.put("error", "Failed to save data. " + e.getMessage());
            e.printStackTrace();
            return new ApiResponseDTO<>(500, "Internal Server Error", result);
        }
    }
}

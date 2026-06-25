package id.co.msiglife.pollux.schduler.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import id.co.msiglife.pollux.schduler.dto.ApiResponseDTO;
import id.co.msiglife.pollux.schduler.entity.BcaData;
import id.co.msiglife.pollux.schduler.entity.ListPromotionPackage;
import id.co.msiglife.pollux.schduler.entity.MasterEmail;
import id.co.msiglife.pollux.schduler.repository.ListPromotionPackageRepository;
import id.co.msiglife.pollux.schduler.repository.MappingBcaRepository;
import id.co.msiglife.pollux.schduler.repository.MasterEmailRepository;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static id.co.msiglife.pollux.schduler.util.ConvertUtil.stringBlank;

@Service
public class OrderBcaService {

    @Autowired
    private MappingBcaRepository mappingBcaRepository;

    @Autowired
    private ListPromotionPackageRepository listPromotionPackageRepository;

    @Autowired
    private MasterEmailRepository masterEmailRepository;

    @Value("${spring.custom.storage.path}")
    private String STORAGE_PATH;

    @Value("${spring.custom.mailpool-attachment.path}")
    private String ATTACHMENT_PATH;

    @PersistenceContext
    private EntityManager entityManager;

    public void sendEmailOrder() {
        try {
            // step 1: get unsent order to ao
            List<BcaData> bcaDataList = mappingBcaRepository.findUnsentOrdersToAo();

            if (bcaDataList == null || bcaDataList.isEmpty()) {
                System.out.println("No BCA orders to send to AO");
                return;
            } else {
                System.out.println("Process: " + bcaDataList.size() + " BCA orders to send to AO");
            }

            // iterate each order
            for (BcaData bcaData : bcaDataList) {

                String subject = "BCA - " + (bcaData.getTipeTrans() == 1 ? "Order" : "Revision") + " Baru an " + bcaData.getNamaLengkapTertanggung();
                String kodeCabang = bcaData.getKodeCabangPengirimOrder();
                String namaCabangPengirim = bcaData.getCabangPengirimOrder();

                // if revision
                if (bcaData.getTipeTrans() == 2) {
                    Optional<BcaData> tempBcaData = mappingBcaRepository.findByRegSpajAndTipeTrans(bcaData.getRegSpaj(), 1);
                    if (tempBcaData.isPresent()) {
                        kodeCabang = tempBcaData.get().getKodeCabangPengirimOrder();
                        namaCabangPengirim = tempBcaData.get().getCabangPengirimOrder();
                    } else {
                        System.out.println("Order Bca with reg spaj: " + bcaData.getRegSpaj() + " not found");
                        return;
                    }
                }

                String message =
                        "Dear All,\n\n" +
                        (bcaData.getTipeTrans() == 1 ? "Order" : "Revision") + " baru " + bcaData.getReferenceNo() + " atas nama " +
                        bcaData.getNamaLengkapTertanggung() + " dari " + kodeCabang + " - " + namaCabangPengirim +
                        " sudah dikirimkan. Mohon untuk segera diproses.\n\nRegards,\nBCA";

                // step 2: generate pdf
                String outputPdfPath = "";
                ApiResponseDTO<Map<String, Object>> generatePdfBcaResponse = generatePdfBca(bcaData);
                if (generatePdfBcaResponse.getCode() != 200) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    throw new RuntimeException(generatePdfBcaResponse.getMessage());
                }

                outputPdfPath = (String) generatePdfBcaResponse.getData().get("outputPdf");
                if (outputPdfPath.isEmpty()) {
                    throw new RuntimeException("Output Pdf Path is empty");
                }

                // step 3: send email to ao

                // Fetch the email ID from the SQL function using a native SQL query
                String generatedId = entityManager.createNativeQuery("SELECT EKA.PAC_COUNTER.GET_EMAIL_ID FROM DUAL").getSingleResult().toString();
                Date sysDate = (Date) entityManager.createNativeQuery("SELECT SYSDATE FROM DUAL").getSingleResult();
                System.out.println("Printed generated email ID: " + generatedId);

                // Define the folder path for attachments
                // (ex: //storage.sinarmasmsiglife.co.id/mailpools/attachments/2023/09/20230923661730)
                String attachmentFolderPath = ATTACHMENT_PATH + generatedId.substring(0, 4) + "/" + generatedId.substring(4, 6) + "/" + generatedId;
                System.out.println("Printed attachment folder: " + attachmentFolderPath);

                // Create the folder if it doesn't exist
                File folder = new File(attachmentFolderPath);
                if (!folder.exists()) {
                    if (!folder.mkdirs()) {
                        throw new IOException("Failed to create directory: " + attachmentFolderPath);
                    }
                }

                // Source file path
                System.out.println("Printed source file path: " + outputPdfPath);
                Path sourcePath = Paths.get(outputPdfPath).toAbsolutePath();

                // Validate source file
                if (!Files.exists(sourcePath) || !Files.isReadable(sourcePath)) {
                    throw new IOException("Source file does not exist or is not readable: " + outputPdfPath);
                }

                // Destination file path
                String destinationFilePath = attachmentFolderPath + "/" + bcaData.getRegSpaj() +"_BCA" + (bcaData.getTipeTrans() == 1 ? "_Order_" : "_Revision_") + bcaData.getNoUrut() + ".pdf";
                System.out.println("Printed destination file path: " + destinationFilePath);
                Path destinationPath = Paths.get(destinationFilePath).toAbsolutePath();

                // Copy the file
                Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);

                // Check if the copied file is readable
                if (!Files.isReadable(destinationPath)) {
                    throw new IOException("The copied file is not readable: " + destinationFilePath);
                }

                System.out.println("File copied successfully to: " + destinationFilePath);

                MasterEmail email = new MasterEmail();
                email.setId(generatedId);
                email.setSystem("POLLUX");
                email.setShowSystem(0);
                email.setShowFooter(0);
                email.setFrom("cosmos@msiglife.co.id");
                email.setFromAlias("Notifikasi POLLUX");
                email.setTo("MRI.bcajkt@msiglife.co.id;mrisurabaya@msiglife.co.id;");
                email.setCc(null);
                email.setBcc(null);
                email.setSubject(subject);
                email.setMessage(message);
                email.setMecId(0);
                email.setHtml(1);
                email.setStatus(0);
                email.setStatusMessage(null);
                email.setCount(0);
                email.setUserId(0); // system user
                email.setInputDate(sysDate);
                email.setSentDate(null);
                email.setSchedulerDate(sysDate);
                email.setPriority(50);
                email.setJenisId(50);
                email.setFlagNew(0);
                email.setRegSpaj(bcaData.getRegSpaj());
                email.setPathAttach(destinationPath.toString());

                masterEmailRepository.save(email);

                // step 4: update send to ao flag
                bcaData.setSendEmailToAo(1);
            }

            mappingBcaRepository.saveAll(bcaDataList);

        } catch (IOException e) {
            System.err.println("An error occurred during the file operation: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error log: ");
            e.printStackTrace();
        }
    }

    private ApiResponseDTO<Map<String, Object>> generatePdfBca(BcaData bcaData) {
        Map<String, Object> result = new HashMap<>();

        try {
            ListPromotionPackage listPromotionPackage = listPromotionPackageRepository.findByReferenceNoFirstOnly(bcaData.getReferenceNo());
            String promotionPackage = listPromotionPackage == null ? "" : listPromotionPackage.getPromotionDesc();

            BigDecimal up = bcaData.getNilaiPertanggungan() == null ? BigDecimal.ZERO : BigDecimal.valueOf(bcaData.getNilaiPertanggungan());
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            String dob = stringBlank(bcaData.getTanggalLahir()) ? "-" : new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("ddMMyyyy").parse(bcaData.getTanggalLahir()));
            String pob = stringBlank(bcaData.getTempatLahir()) ? "-" : bcaData.getTempatLahir();
            String inputRevisionDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(bcaData.getTglInsertApi());
            String printDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());

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
            content = content.replace("{tipe_transaksi_code}", bcaData.getTipeTrans().toString());
            content = content.replace("{tipe_transaksi}", (bcaData.getTipeTrans() == 2) ? "Revision" : (bcaData.getTipeTrans() == 3) ? "Cancellation" : "Order");
            content = content.replace("{order_id}", stringBlank(bcaData.getOrderId()) ? "-" : bcaData.getOrderId());
            content = content.replace("{reg_spaj}", stringBlank(bcaData.getRegSpaj()) ? "-" : bcaData.getRegSpaj());
            content = content.replace("{reference_number}", stringBlank(bcaData.getReferenceNo()) ? "-" : bcaData.getReferenceNo());
            content = content.replace("{nama_cabang}", stringBlank(bcaData.getNamaCabang()) ? "-" : bcaData.getNamaCabang());
            content = content.replace("{id_number_tertanggung}", stringBlank(bcaData.getNomorIdTertanggung()) ? "-" : bcaData.getNomorIdTertanggung());
            content = content.replace("{kewarganegaraan}", stringBlank(bcaData.getKewarganegaraan()) ? "-" : bcaData.getKewarganegaraan());
            content = content.replace("{fullname_tertanggung}", stringBlank(bcaData.getNamaLengkapTertanggung()) ? "-" : bcaData.getNamaLengkapTertanggung());
            content = content.replace("{gender}", stringBlank(bcaData.getJenisKelamin()) ? "-" : (bcaData.getJenisKelamin().equalsIgnoreCase("F") ? "Female" : "Male"));
            content = content.replace("{pekerjaan}", stringBlank(bcaData.getPekerjaan()) ? "-" : bcaData.getPekerjaan());
            content = content.replace("{company_name_other}", stringBlank(bcaData.getCompanyNameOther()) ? "-" : bcaData.getCompanyNameOther());
            content = content.replace("{home_phone_number}", stringBlank(bcaData.getTeleponRumah()) ? "-" : bcaData.getTeleponRumah());
            content = content.replace("{office_phone_number}", stringBlank(bcaData.getTeleponKantor()) ? "-" : bcaData.getTeleponKantor());
            content = content.replace("{mobile_number}", stringBlank(bcaData.getHandphone()) ? "-" : bcaData.getHandphone());
            content = content.replace("{email}", stringBlank(bcaData.getEmail()) ? "-" : bcaData.getEmail());
            content = content.replace("{pob_dob}", (dob.equals("-") || pob.equals("-")) ? "-" : pob + ", " + dob);
            content = content.replace("{masa_pertanggungan}", bcaData.getMasaPertanggungan() == null ? "-" : bcaData.getMasaPertanggungan().toString());
            content = content.replace("{nilai_pertanggungan}", currencyFormat.format(up));
            content = content.replace("{keterangan}", stringBlank(bcaData.getKeterangan()) ? "-" : bcaData.getKeterangan());
            content = content.replace("{input_date}", inputRevisionDate);
            content = content.replace("{print_date}", printDate);
            content = content.replace("{pic_bca}", stringBlank(bcaData.getPicBca()) ? "-" : bcaData.getPicBca());
            content = content.replace("{pic_order}", stringBlank(bcaData.getPicBca()) ? "-" : bcaData.getPicOrder());
            content = content.replace("{covernote_file_name}", stringBlank(bcaData.getNamaFileCovernote()) ? "-" : bcaData.getNamaFileCovernote());
            content = content.replace("{diskon_program}", stringBlank(String.valueOf(bcaData.getPersenProgramDiskonBca())) ? "-" : String.valueOf(bcaData.getPersenProgramDiskonBca()));
            content = content.replace("{diskon_maskapai}", stringBlank(String.valueOf(bcaData.getPersenDiskonMaskapai())) ? "-" : String.valueOf(bcaData.getPersenDiskonMaskapai()));
            content = content.replace("{diskon_special}", stringBlank(String.valueOf(bcaData.getPersenDiskonBca())) ? "-" : String.valueOf(bcaData.getPersenDiskonBca()));

            System.out.println("Printed HTML content: \n" + content);

            // create string HTML to a JSoup Document to render XHTML
            Document document = Jsoup.parse(content);
            document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);

            // check if folder hasn't existed
            String folderPath = STORAGE_PATH + bcaData.getRegSpaj();

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
            String outputPdf = folderPath + "/" + bcaData.getRegSpaj() + "_BCA" + (bcaData.getTipeTrans() == 1 ? "_Order_" : "_Revision_") + bcaData.getNoUrut() + ".pdf";

            try (OutputStream os = Files.newOutputStream(Paths.get(outputPdf))) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.withUri(outputPdf);
                builder.toStream(os);
                builder.withW3cDocument(new W3CDom().fromJsoup(document), folderPath + "/");
                builder.run();
            }

            result.put("outputPdf", outputPdf);

            return new ApiResponseDTO<>(200, "PDF BCA created successfully.", result);

        } catch (IllegalArgumentException e) {
            System.out.println("Error log: ");
            result.put("error", e.getMessage());
            e.printStackTrace();
            return new ApiResponseDTO<>(400, "Bad request", result);
        } catch (Exception e) {
            System.out.println("Error log: ");
            result.put("error", "Failed to save data. " + e.getMessage());
            e.printStackTrace();
            return new ApiResponseDTO<>(500, "Internal Server Error", result);
        }
    }
}

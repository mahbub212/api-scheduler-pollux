package id.co.msiglife.pollux.schduler.service;

import id.co.msiglife.pollux.schduler.dto.CancelPolisDTO;
import id.co.msiglife.pollux.schduler.dto.CancelSpajkDTO;
import id.co.msiglife.pollux.schduler.entity.*;
import id.co.msiglife.pollux.schduler.entity.CancelPolis.*;
import id.co.msiglife.pollux.schduler.entity.Journal.MstPtcJm;
import id.co.msiglife.pollux.schduler.entity.Journal.MstPtcTm;
import id.co.msiglife.pollux.schduler.repository.*;
import id.co.msiglife.pollux.schduler.repository.CancelPolis.*;
import id.co.msiglife.pollux.schduler.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CancelPolisService {

    @Value("${spring.custom.storage.path}")
    private String STORAGE_PATH;

    @Value("${spring.custom.storage-polis.path}")
    private String STORAGE_POLIS_PATH;

    @Autowired
    private MasterBillingRepository masterBillingRepository;

    @Autowired
    private MasterPolicyRepository masterPolicyRepository;

    @Autowired
    private MasterProductionRepository masterProductionRepository;

    @Autowired
    private MasterDetProductionRepository masterDetProductionRepository;

    @Autowired
    private MasterPositionSPAJRepository masterPositionSPAJRepository;

    @Autowired
    private PtcJmRepository ptcJmRepository;

    @Autowired
    private PtcTmRepository ptcTmRepository;

    @Autowired
    private MasterEndorsRepository masterEndorsRepository;

    @Autowired
    private MasterDetEndorsRepository masterDetEndorsRepository;

    @Autowired
    private MasterInsuredRepository masterInsuredRepository;

    @Autowired
    private MasterProductInsuredRepository masterProductInsuredRepository;

    @Autowired
    private MasterProductSetupRepository masterProductSetupRepository;

    @Autowired
    private MasterAutoCancelEmailLogRepository masterAutoCancelEmailLogRepository;

    @PersistenceContext
    private EntityManager em;

    @Scheduled(cron = "0 40 1 * * ?")
    public void cancelSpajkScheduler(){
        List<CancelPolisDTO> cancelPolisDTOList = getCancelPolisList();
        System.out.println("=================== START EXECUTING TASK: CANCEL SPAJK (" + cancelPolisDTOList.size() + ") Data ===================");
        processCancelPolisList(cancelPolisDTOList);
        System.out.println("=================== FINISHED EXECUTING TASK: CANCEL SPAJK ===================");
    }

    @Scheduled(cron = "0 40 5 * * ?")
    public void retryCancelSpajkScheduler(){
        List<CancelPolisDTO> cancelPolisDTOList = getCancelPolisList();
        System.out.println("=================== RETRY EXECUTING TASK: CANCEL SPAJK (" + cancelPolisDTOList.size() + ") Data ===================");
        processCancelPolisList(cancelPolisDTOList);
        System.out.println("=================== FINISHED RETRYING EXECUTING TASK: CANCEL SPAJK ===================");
    }

    @Transactional
    private void processCancelPolisList(List<CancelPolisDTO> cancelPolisDTOList) {
        if (cancelPolisDTOList == null || cancelPolisDTOList.isEmpty()) return;

        List<MasterEndors> endorses = new ArrayList<>();
        List<MasterDetEndors> detEndorses = new ArrayList<>();
        List<MasterPositionSPAJ> positions = new ArrayList<>();
        List<MasterProduction> prods = new ArrayList<>();
        List<MasterDetProduction> detProds = new ArrayList<>();
        List<MasterBilling> masterBillingList = new ArrayList<>();
        List<MstPtcTm> tms = new ArrayList<>();
        List<MstPtcJm> jms = new ArrayList<>();

        Date dateNow = ptcJmRepository.getTruncatedSysdate();
        BigDecimal kurs = new BigDecimal(1);

        for (int i = 0; i < cancelPolisDTOList.size(); i++) {
            CancelPolisDTO dto = cancelPolisDTOList.get(i);
            MasterPolicy masterPolicy = masterPolicyRepository.findByRegSpaj(dto.getRegSpaj());
            MasterBilling masterBilling = masterBillingRepository.findByRegSpaj(dto.getRegSpaj());
            MasterInsured masterInsured = masterInsuredRepository.findByRegSpajAndInsuredNo(dto.getRegSpaj(), 1);
            MasterProductInsured masterProductInsured = masterProductInsuredRepository.findMainProduct(dto.getRegSpaj(), 0, 1).get(0);
            MasterProductSetup masterProductSetup = masterProductSetupRepository.findByProductSetupId(masterPolicy.getProductId());
            String noJm = ptcJmRepository.getNoJm();

            if(!masterPolicy.getLkuId().equals("1")){
                SimpleDateFormat years = new SimpleDateFormat("yyyy");
                SimpleDateFormat months = new SimpleDateFormat("MM");
                String yearNow = years.format(dateNow);
                String monthNow = months.format(dateNow);

                kurs = ptcJmRepository.getKursLkuId02(yearNow, monthNow);
            }

            // 1) MST_ENDORS
            // for mst endors id (SELECT eka.pac_counter.GET_TAX_ADJ_ID() FROM DUAL;)
            MasterEndors masterEndors = insertMasterEndors(dto, dateNow);
            endorses.add(masterEndors);

            // 2) MST_DET_ENDORS
            MasterDetEndors masterDetEndors = insertMasterDetEndors(dto, masterEndors, dateNow);
            detEndorses.add(masterDetEndors);

            // 3) POSITION SPAJ
            MasterPositionSPAJ masterPositionSPAJ = insertMasterPositionSpaj(dto, masterEndors, masterInsured);
            positions.add(masterPositionSPAJ);

            // 4) MST_PRODUCTION
            MasterProduction masterProduction = insertMasterProduction(masterPolicy, dateNow, kurs, masterBilling, dto.getRegSpaj());
            masterProduction.setNoJm(noJm);
            prods.add(masterProduction);

            // 5) MST_DET_PRODUCTION
            MasterDetProduction masterDetProduction = insertMasterDetProduction(masterPolicy, dateNow, masterProductInsured, masterProductSetup, dto.getRegSpaj());
            detProds.add(masterDetProduction);

            // 6) MST_PTC_TM (no_jm berasal dari generator mu)
            MstPtcTm mstPtcTm = insertMstTm();
            tms.add(mstPtcTm);

            // 7) MST_PTC_JM (baris kas/debet/creditnya)
            MstPtcJm mstPtcJm = insertMstJm();
            jms.add(mstPtcJm);

            // 8) update master Billing
            masterBilling.setMsbiNoJmBtl(masterProduction.getNoJm());
            masterBilling.setMsbiTglBatal(masterEndors.getMsenInputDate());
            masterBilling.setLspdId(ConvertUtil.safeLong(25));
            masterBilling.setMsbiActive(0);
            masterBillingList.add(masterBilling);

            copyingFilesPolis(dto);
        }

        flushAndClear(endorses, detEndorses, positions, prods, detProds, tms, jms, masterBillingList);

        // 9) BULK UPDATE status policy & billing
        masterPolicyRepository.updatePolisCanceled(
                cancelPolisDTOList.stream()
                        .map(CancelPolisDTO::getRegSpaj)
                        .collect(Collectors.toList())
        );

        // 10) updatePolisCanceled
        masterAutoCancelEmailLogRepository.updatePolisCanceled(cancelPolisDTOList.stream()
                .map(CancelPolisDTO::getRegSpaj)
                .collect(Collectors.toList())
        );
    }

    private void flushAndClear(List<?>... buckets) {
        for (List<?> b : buckets) {
            if (b == null || b.isEmpty()) continue;
            if (b.get(0) instanceof MasterEndors)        masterEndorsRepository.saveAll((List<MasterEndors>) b);
            else if (b.get(0) instanceof MasterDetEndors) masterDetEndorsRepository.saveAll((List<MasterDetEndors>) b);
            else if (b.get(0) instanceof MasterPositionSPAJ) masterPositionSPAJRepository.saveAll((List<MasterPositionSPAJ>) b);
            else if (b.get(0) instanceof MasterProduction)   masterProductionRepository.saveAll((List<MasterProduction>) b);
            else if (b.get(0) instanceof MasterDetProduction) masterDetProductionRepository.saveAll((List<MasterDetProduction>) b);
            else if (b.get(0) instanceof MstPtcTm)           ptcTmRepository.saveAll((List<MstPtcTm>) b);
            else if (b.get(0) instanceof MstPtcJm)           ptcJmRepository.saveAll((List<MstPtcJm>) b);
            else if (b.get(0) instanceof MasterBilling)      masterBillingRepository.saveAll((List<MasterBilling>) b);
            b.clear();
        }
        em.flush();
        em.clear();
    }

    private String copyingFilesPolis(CancelPolisDTO dto){
        try {
            String sourceFilePath = STORAGE_PATH + "/" + dto.getRegSpaj()  + "/" + dto.getRegSpaj() + "_SURAT_KONFIRMASI_PEMBATALAN_SERTIFIKAT.pdf";
            Path sourcePath = Paths.get(sourceFilePath).toAbsolutePath();
            Path destinationPath = Paths.get(STORAGE_POLIS_PATH).toAbsolutePath();;
            // Validasi keberadaan file
            if (Files.exists(sourcePath) && Files.isReadable(sourcePath)) {

                // destination file path
                String destinationFilePath = STORAGE_POLIS_PATH + "/" + dto.getRegSpaj() + "_SURAT_KONFIRMASI_PEMBATALAN_SERTIFIKAT.pdf";
                destinationPath = Paths.get(destinationFilePath).toAbsolutePath();

                // copy the file
                Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);

                // check if the copies of both files are readable
                if (!Files.isReadable(destinationPath)) {
                    try {
                        throw new IOException("The copied Surat Konfirmasi Pembatalan Sertifikat file is not readable: " + destinationFilePath);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return destinationPath.toString();
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private MasterPositionSPAJ insertMasterPositionSpaj(CancelPolisDTO CancelPolisDTO, MasterEndors masterEndors, MasterInsured masterInsured) {
        MasterPositionSPAJ masterPositionSPAJ = new MasterPositionSPAJ();
        masterPositionSPAJ.setRegSpaj(CancelPolisDTO.getRegSpaj());
        masterPositionSPAJ.setPositionDate(masterEndors.getMsenInputDate());
        masterPositionSPAJ.setLspdId(13);
        masterPositionSPAJ.setLsspId(8);
        //MST_INSURED"."LSSA_ID
        masterPositionSPAJ.setLssaId(masterInsured.getLssaId());
        masterPositionSPAJ.setLusId(0);
        masterPositionSPAJ.setDesc(masterEndors.getMsenAlasan());
        return masterPositionSPAJ;
    }

    private MasterDetEndors insertMasterDetEndors(CancelPolisDTO CancelPolisDTO, MasterEndors masterEndors, Date now) {
        MasterDetEndors masterDetEndors = new MasterDetEndors();
        masterDetEndors.setMsenEndorsNo(masterEndors.getMsenEndorsNo());
        masterDetEndors.setMsenfNumber(ConvertUtil.safeLong(1));
        masterDetEndors.setLsjeId(ConvertUtil.safeLong(27));
        masterDetEndors.setMsteInsuredNo(ConvertUtil.safeLong(1));
        return masterDetEndors;
    }

    private MasterEndors insertMasterEndors(CancelPolisDTO CancelPolisDTO, Date dateNow) {
        MasterEndors masterEndors = new MasterEndors();
        LocalDate localDate = dateNow.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        masterEndors.setRegSpaj(CancelPolisDTO.getRegSpaj());
        masterEndors.setMsenInternal(0);
        masterEndors.setMsenAlasan("Pembatalan Polis, Premi belum dibayarkan.");
        masterEndors.setMsenInputDate(date);
        masterEndors.setMsenActiveDate(dateNow);
        masterEndors.setLspdId(99);
        masterEndors.setLusId(0);
        masterEndors.setMsenAutoRider(0);
        masterEndors.setFlagPs(0);
        masterEndors.setMsenFlagUw(0);
        masterEndors.setMsenFlagTtp(0);
        masterEndors.setMsenAksepUw(0);
        masterEndors.setMsenIssueSpaj(0);
        masterEndors.setMsenReproses(0);
        masterEndors.setMsenSeq(ConvertUtil.safeLong(1));
        return masterEndors;
    }

    private MasterProduction insertMasterProduction(MasterPolicy masterPolicy, Date dateNow, BigDecimal kurs, MasterBilling masterBilling, String regSpaj) {
        MasterProduction masterProduction = new MasterProduction();
        masterProduction.setLstbId(masterPolicy.getLstbId());
        masterProduction.setRegSpaj(regSpaj);
        masterProduction.setMsproProdKe(2);
        masterProduction.setMsproProdDate(dateNow);
        masterProduction.setMsbiTahunKe(1);
        masterProduction.setMsbiPremiKe(1);
        masterProduction.setMsproJnProd("3");
        masterProduction.setLkuId("01");
        masterProduction.setMsproNilaiKurs(kurs);//
        masterProduction.setMsproBegDate(masterPolicy.getBegDate());
        masterProduction.setMsproEndDate(masterPolicy.getEndDate());
        masterProduction.setMsproPolicyCost(masterBilling.getMsbiPolicyCost());
        masterProduction.setMsproCardCost(masterBilling.getMsbiTtlCardCost());
        masterProduction.setMsproStamp(masterBilling.getMsbiStamp());
        masterProduction.setMsproJmlMember(1);
        masterProduction.setLcaId(masterPolicy.getLcaId());
        masterProduction.setLwkId(masterPolicy.getLwkId());
        masterProduction.setLsrgId(Long.valueOf(masterPolicy.getLsrgId()));
        masterProductionRepository.save(masterProduction);
        return masterProduction;
    }

    private MasterDetProduction insertMasterDetProduction(MasterPolicy masterPolicy, Date dateNow, MasterProductInsured masterProductInsured, MasterProductSetup masterProductSetup, String regSpaj){

        MasterDetProduction masterDetProduction = new MasterDetProduction();
        LocalDate localDate = dateNow.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        masterDetProduction.setLstbId(masterPolicy.getLstbId());
        masterDetProduction.setRegSpaj(regSpaj);
        masterDetProduction.setMsproProdKe(1);
        masterDetProduction.setMsproProdDate(localDate);
        masterDetProduction.setLsbsId(masterProductInsured.getLsbsId());
        masterDetProduction.setLsdbsNumber(masterProductSetup.getProductNumber());
        masterDetProduction.setMsbiTahunKe(1);
        masterDetProduction.setMsbiPremiKe(1);
        masterDetProduction.setMsdprPremium(BigDecimal.valueOf(masterProductInsured.getPremium()));
        masterDetProduction.setMsdprDiscount(BigDecimal.valueOf(masterProductInsured.getDiscount()));
        masterDetProduction.setMsdprTsi(masterProductInsured.getTsi());
        masterDetProduction.setMsdprUrut(masterProductInsured.getUrut());

        masterDetProductionRepository.save(masterDetProduction);
        return masterDetProduction;
    }

    private MstPtcTm insertMstTm(){
        MstPtcTm mstPtcTm = new MstPtcTm();
        ptcTmRepository.save(mstPtcTm);
        return mstPtcTm;
    }

    private MstPtcJm insertMstJm(){
        MstPtcJm mstPtcJm = new MstPtcJm();
        ptcJmRepository.save(mstPtcJm);
        return mstPtcJm;
    }

    private List<CancelPolisDTO> getCancelPolisList() {
        List<Object[]> resultList = masterPolicyRepository.findPolicyTobeCancelled();
        List<CancelPolisDTO> policyToBeCancelledList = new ArrayList<>();
        for (Object[] row : resultList) {
            CancelPolisDTO dto = new CancelPolisDTO();
            dto.setRegSpaj(ConvertUtil.safeString(row[0]));
            policyToBeCancelledList.add(dto);
        }
        return policyToBeCancelledList;
    }

}

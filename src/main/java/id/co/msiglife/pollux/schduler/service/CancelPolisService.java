package id.co.msiglife.pollux.schduler.service;

import id.co.msiglife.pollux.schduler.dto.CancelPolisDTO;
import id.co.msiglife.pollux.schduler.entity.*;
import id.co.msiglife.pollux.schduler.entity.polis.*;
import id.co.msiglife.pollux.schduler.entity.Journal.MstPtcJm;
import id.co.msiglife.pollux.schduler.entity.Journal.MstPtcTm;
import id.co.msiglife.pollux.schduler.exception.NotBalancedException;
import id.co.msiglife.pollux.schduler.projection.CancelJmProjection;
import id.co.msiglife.pollux.schduler.repository.*;
import id.co.msiglife.pollux.schduler.repository.polis.*;
import id.co.msiglife.pollux.schduler.service.JournalService.JournalService;
import id.co.msiglife.pollux.schduler.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CancelPolisService {

    @Value("${spring.custom.storage.path}")
    private String STORAGE_PATH;

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

    @Autowired
    MasterWFDocumentRepository masterWFDocumentRepository;

    @Autowired
    MasterWFDocumentHistoryRepository masterWFDocumentHistoryRepository;

    @Autowired
    ListWFConfigRepository listWFConfigRepository;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private JournalService journalService;

    @Transactional
    public void processCancelPolisList(List<CancelPolisDTO> cancelPolisDTOList) {
        if (cancelPolisDTOList == null || cancelPolisDTOList.isEmpty()) return;

        List<MasterEndors> endorses = new ArrayList<>();
        List<MasterDetEndors> detEndorses = new ArrayList<>();
        List<MasterPositionSPAJ> positions = new ArrayList<>();
        List<MasterProduction> prods = new ArrayList<>();
        List<MasterDetProduction> detProds = new ArrayList<>();
        List<MasterBilling> masterBillingList = new ArrayList<>();
        List<MstPtcTm> tms = new ArrayList<>();
        List<MstPtcJm> jmList = new ArrayList<>();

        Date dateNow = ptcJmRepository.getTruncatedSysdate();
        BigDecimal kurs = new BigDecimal(1);

        List<CancelPolisDTO> cancelPolisProceed = new ArrayList<>();

        for (int i = 0; i < cancelPolisDTOList.size(); i++) {
            CancelPolisDTO dto = cancelPolisDTOList.get(i);
            try {
                MasterPolicy masterPolicy = masterPolicyRepository.findByRegSpaj(dto.getRegSpaj());
                MasterBilling masterBilling = masterBillingRepository.findByRegSpaj(dto.getRegSpaj());
                MasterInsured masterInsured = masterInsuredRepository.findByRegSpajAndInsuredNo(dto.getRegSpaj(), 1);
                MasterProductInsured masterProductInsured = masterProductInsuredRepository.findMainProduct(dto.getRegSpaj(), 0, 1).get(0);
                MasterProductSetup masterProductSetup = masterProductSetupRepository.findByProductSetupId(masterPolicy.getProductId());
                MasterProduction masterProduction = masterProductionRepository.findByRegSpajAndMsproProdKe(dto.getRegSpaj(), 1);
                MasterWFDocument masterWFDocument = masterWFDocumentRepository.findMasterWFDocumentByDocNumberAndLswfTypeId(dto.getRegSpaj(), 78);

                String noJm = ptcJmRepository.getNoJm();
                String noEndors = masterPolicyRepository.getNoEndors();

                if (!masterPolicy.getLkuId().equals("1")) {
                    SimpleDateFormat years = new SimpleDateFormat("yyyy");
                    SimpleDateFormat months = new SimpleDateFormat("MM");
                    String yearNow = years.format(dateNow);
                    String monthNow = months.format(dateNow);

                    kurs = ptcJmRepository.getKursLkuId02(yearNow, monthNow);
                }

                if (ConvertUtil.bigDecimalNEQZero(ptcJmRepository.isBalanceJournal(masterProduction.getNoJm()))) {
                    System.out.println("Journal Master Not Balance Reg Spaj : " + dto.getRegSpaj() + " No Jm : " + masterProduction.getNoJm());
                    throw new NotBalancedException("NoJM " + noJm);
                }

                // 1) MST_ENDORS
                // for mst endors id (SELECT eka.pac_counter.GET_TAX_ADJ_ID() FROM DUAL;)
                MasterEndors masterEndors = insertMasterEndors(dto, noEndors, dateNow);

                // 2) MST_DET_ENDORS
                MasterDetEndors masterDetEndors = insertMasterDetEndors(dto, masterEndors, dateNow);

                // 3) MST_PRODUCTION
                MasterProduction masterProductionCancel = insertMasterProduction(masterPolicy, dateNow, kurs, masterBilling, dto.getRegSpaj());
                masterProductionCancel.setNoJm(noJm);


                // 4) MST_DET_PRODUCTION
                MasterDetProduction masterDetProduction = insertMasterDetProduction(masterPolicy, dateNow, masterProductInsured, masterProductSetup, dto.getRegSpaj());
                //masterDetProductionRepository.copyDetProductionBySpaj(dto.getRegSpaj());

                // 5) POSITION SPAJ
                MasterPositionSPAJ masterPositionSPAJ = insertMasterPositionSpaj(dto, masterEndors, masterInsured);

                // insert WF engine
                ListWFConfig wfConfig = listWFConfigRepository.findListWFConfigByTypeIdAndStateIdFromAndStateIdTo(
                        masterWFDocument.getLswfTypeId(), masterWFDocument.getLswfStateId(), -7);

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

                // 6) MST_PTC_TM (no_jm berasal dari generator mu)
                //pindah sekalian Tm dan Jm

                // 7) MST_PTC_JM_LIST (baris kas/debet/creditnya)
                List<CancelJmProjection> rows = ptcJmRepository.buildCancelJmRows(
                        noJm, masterProduction.getNoJm(), dto.getRegSpaj()
                );
                journalService.insertMstTmJm(rows, masterProduction, dateNow, noJm, ptcJmRepository);

                System.out.println(ptcJmRepository.journalCreated(noJm));

                endorses.add(masterEndors);
                detEndorses.add(masterDetEndors);
                prods.add(masterProductionCancel);
                detProds.add(masterDetProduction);
                positions.add(masterPositionSPAJ);
                cancelPolisProceed.add(dto);

                // 9) update master Billing
                masterBilling.setMsbiNoJmBtl(noJm);
                masterBilling.setMsbiTglBatal(masterEndors.getMsenInputDate());
                masterBilling.setLspdId(ConvertUtil.safeLong(25));
                masterBilling.setMsbiActive(0);
                masterBillingList.add(masterBilling);
            } catch(NotBalancedException e){
                System.out.println("Journal Cancel Not Balance RegSpaj: " + dto.getRegSpaj() + " -> " + e.getMessage());
                // lanjut ke item berikutnya; data JM/TM utk item ini sudah rollback
                continue;
            }
        }

        flushAndClear(endorses, detEndorses, positions, detProds, prods, masterBillingList);

        // 9) BULK UPDATE status policy & billing
        masterPolicyRepository.updatePolisCanceled(
                cancelPolisProceed.stream()
                        .map(CancelPolisDTO::getRegSpaj)
                        .collect(Collectors.toList())
        );

        // 10) updatePolisCanceled
        masterAutoCancelEmailLogRepository.updatePolisCanceled(cancelPolisProceed.stream()
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

    private MasterDetEndors insertMasterDetEndors(CancelPolisDTO CancelPolisDTO, MasterEndors masterEndors, Date now) {
        MasterDetEndors masterDetEndors = new MasterDetEndors();
        masterDetEndors.setMsenEndorsNo(masterEndors.getMsenEndorsNo());
        masterDetEndors.setMsenfNumber(ConvertUtil.safeLong(1));
        masterDetEndors.setLsjeId(ConvertUtil.safeLong(27));
        masterDetEndors.setMsteInsuredNo(ConvertUtil.safeLong(1));
        return masterDetEndors;
    }

    private MasterEndors insertMasterEndors(CancelPolisDTO CancelPolisDTO, String noEndors, Date dateNow) {
        MasterEndors masterEndors = new MasterEndors();
        LocalDate localDate = dateNow.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        masterEndors.setMsenEndorsNo(noEndors);
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
        return masterProduction;
    }

    private MasterDetProduction insertMasterDetProduction(MasterPolicy masterPolicy, Date dateNow, MasterProductInsured masterProductInsured, MasterProductSetup masterProductSetup, String regSpaj){
        MasterDetProduction masterDetProduction = new MasterDetProduction();
        masterDetProduction.setLstbId(masterPolicy.getLstbId());
        masterDetProduction.setRegSpaj(regSpaj);
        masterDetProduction.setMsproProdKe(2);
        LocalDate localDate = dateNow.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        masterDetProduction.setMsproProdDate(localDate);
        masterDetProduction.setLsbsId(masterProductInsured.getLsbsId());
        masterDetProduction.setLsdbsNumber(masterProductSetup.getProductNumber());
        masterDetProduction.setMsbiTahunKe(1);
        masterDetProduction.setMsbiPremiKe(1);
        masterDetProduction.setMsdprPremium(BigDecimal.valueOf(masterProductInsured.getPremium()));
        masterDetProduction.setMsdprDiscount(BigDecimal.valueOf(masterProductInsured.getDiscount()));
        masterDetProduction.setMsdprTsi(masterProductInsured.getTsi());
        masterDetProduction.setMsdprUrut(masterProductInsured.getUrut());
        return masterDetProduction;
    }



    public List<CancelPolisDTO> getCancelPolisList() {
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

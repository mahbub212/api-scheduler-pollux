package id.co.msiglife.pollux.schduler.service;

import id.co.msiglife.pollux.schduler.dto.CancelPolisDTO;
import id.co.msiglife.pollux.schduler.entity.*;
import id.co.msiglife.pollux.schduler.entity.polis.*;
import id.co.msiglife.pollux.schduler.exception.WfConfigNotFoundException;
import id.co.msiglife.pollux.schduler.projection.CancelJmProjection;
import id.co.msiglife.pollux.schduler.repository.*;
import id.co.msiglife.pollux.schduler.repository.polis.*;
import id.co.msiglife.pollux.schduler.service.JournalService.JournalService;
import id.co.msiglife.pollux.schduler.util.ConvertUtil;
import id.co.msiglife.pollux.schduler.util.StaticValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
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

        // Track successfully processed records

        for (CancelPolisDTO dto : cancelPolisDTOList) {
            try {
                System.out.println("In Progress canceled polis : " + dto.getRegSpaj());
                if (dto.isSyariah()) {
                    processSingleCancelPolis(dto);
                } else {
                    processSingleCancelPolis(dto);
                }
            } catch (WfConfigNotFoundException e) {
                masterAutoCancelEmailLogRepository.updatePolisFailed(dto.getRegSpaj(), StaticValue.SERTIFIKAT_DIBATALKAN_NOTIFICATION_DAYS);
                System.out.println("WF Config Not Found for RegSpaj: " + dto.getRegSpaj() + " -> " + e.getMessage());
            } catch (Exception e) {
                masterAutoCancelEmailLogRepository.updatePolisFailed(dto.getRegSpaj(), StaticValue.SERTIFIKAT_DIBATALKAN_NOTIFICATION_DAYS);
                System.out.println("Unexpected error while processing RegSpaj: " + dto.getRegSpaj() + " -> " + e.getMessage());
            }
        }
    }

    @Transactional
    public void processSingleCancelPolis(CancelPolisDTO dto) {
        masterAutoCancelEmailLogRepository.updatePolisInProgress(dto.getRegSpaj(), StaticValue.SERTIFIKAT_DIBATALKAN_NOTIFICATION_DAYS);

        Date dateNow = ptcJmRepository.getTruncatedSysdate();
        BigDecimal kurs = new BigDecimal(1);

        MasterPolicy masterPolicy = masterPolicyRepository.findByRegSpaj(dto.getRegSpaj());
        MasterBilling masterBilling = masterBillingRepository.findByRegSpaj(dto.getRegSpaj());
        MasterInsured masterInsured = masterInsuredRepository.findByRegSpajAndInsuredNo(dto.getRegSpaj(), 1);
        MasterProductInsured masterProductInsured = masterProductInsuredRepository.findMainProduct(dto.getRegSpaj(), 0, 1).get(0);
        MasterProductSetup masterProductSetup = masterProductSetupRepository.findByProductSetupId(masterPolicy.getProductId());
        MasterProduction masterProduction = masterProductionRepository.findByRegSpajAndMsproProdKe(dto.getRegSpaj(), 1);
        MasterWFDocument masterWFDocument = masterWFDocumentRepository.findMasterWFDocumentByDocNumberAndLswfTypeId(dto.getRegSpaj(), 78);

        // WF Config update
        ListWFConfig wfConfig = listWFConfigRepository.findListWFConfigByTypeIdAndStateIdFromAndStateIdTo(
                masterWFDocument.getLswfTypeId(), masterWFDocument.getLswfStateId(), -7);

        if (wfConfig == null) {
            throw new WfConfigNotFoundException("WF Config Not Found");
        }

        String noJm = ptcJmRepository.getNoJm();
        System.out.println("No Jm : " + noJm);
        String noEndors = masterPolicyRepository.getNoEndors();

        // Handle currency conversion
        if (!masterPolicy.getLkuId().equals("01")) {
            SimpleDateFormat years = new SimpleDateFormat("yyyy");
            SimpleDateFormat months = new SimpleDateFormat("MM");
            String yearNow = years.format(dateNow);
            String monthNow = months.format(dateNow);

            kurs = ptcJmRepository.getKursLkuId02(yearNow, monthNow);
        }

        // Create entities
        MasterEndors masterEndors = makeMasterEndors(dto, noEndors, dateNow);
        MasterDetEndors masterDetEndors = makeMasterDetEndors(dto, masterEndors, dateNow);

        MasterProduction masterProductionCancel = makeMasterProduction(masterPolicy, dateNow, kurs, masterBilling,
                dto.getRegSpaj(), masterProduction.getMsproPolicyCost());
        masterProductionCancel.setNoJm(noJm);

        MasterDetProduction masterDetProduction = makeMasterDetProduction(
                masterPolicy, dateNow, masterProductInsured, masterProductSetup, dto.getRegSpaj()
        );

        MasterPositionSPAJ masterPositionSPAJ = makeMasterPositionSpaj(dto, masterEndors, masterInsured);

        // Build JM rows
        List<CancelJmProjection> rows = ptcJmRepository.buildCancelJmRows(
                noJm, masterProduction.getNoJm(), dto.getRegSpaj()
        );

        journalService.insertMstTmJm(rows, masterProduction, dateNow, noJm);

        masterWFDocument.setLswfStateId(wfConfig.getStateIdTo());
        masterWFDocument.setLswfStatusId(11);

        MasterWFDocumentHistory makeMasterWFDocumentHistory = makeMasterWFDocumentHistory(
                masterWFDocument.getDocId(),
                masterWFDocument.getLswfTypeId(),
                masterWFDocument.getParentDocId(),
                wfConfig.getStateIdFrom(),
                wfConfig.getConfigAction(),
                0,
                wfConfig.getStateIdTo(),
                new Date(),
                null,
                null,
                null,
                masterWFDocument.getDocNumber(),
                wfConfig.getStatusId()
        );

        masterWFDocumentHistoryRepository.save(makeMasterWFDocumentHistory);
        masterEndorsRepository.save(masterEndors);
        masterDetEndorsRepository.save(masterDetEndors);
        masterProductionRepository.save(masterProductionCancel);
        masterDetProductionRepository.save(masterDetProduction);
        masterPositionSPAJRepository.save(masterPositionSPAJ);

        // Update billing
        masterBilling.setMsbiNoJmBtl(noJm);
        masterBilling.setMsbiTglBatal(masterEndors.getMsenInputDate());
        masterBilling.setLspdId(ConvertUtil.safeLong(25));
        masterBilling.setMsbiActive(0);
        masterBillingRepository.save(masterBilling);

        masterPolicyRepository.updatePolisCanceledSingle(dto.getRegSpaj());
        masterAutoCancelEmailLogRepository.updatePolisCanceled(dto.getRegSpaj(), StaticValue.SERTIFIKAT_DIBATALKAN_NOTIFICATION_DAYS);

        // 10) updatePolisCanceled
        masterAutoCancelEmailLogRepository.updatePolisCanceled(cancelPolisProceed.stream()
                .map(CancelPolisDTO::getRegSpaj)
                .collect(Collectors.toList()), StaticValue.SERTIFIKAT_DIBATALKAN_NOTIFICATION_TYPE
        );
    }

    private MasterPositionSPAJ makeMasterPositionSpaj(CancelPolisDTO CancelPolisDTO, MasterEndors masterEndors, MasterInsured masterInsured) {
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

    private MasterWFDocumentHistory makeMasterWFDocumentHistory(Long docId, Integer lswfTypeId, Long parentDocId, Integer lswfStateIdFrom, String lswfConfigAction, Integer lusId, Integer lswfStateIdTo, Date docHistCreated, String docStatusKey, Integer docStatusValue, String docHistRemarks, String docNumber, Integer lswfStatusId) {
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
        return masterWFDocumentHistory;
    }

    private MasterDetEndors makeMasterDetEndors(CancelPolisDTO CancelPolisDTO, MasterEndors masterEndors, Date now) {
        MasterDetEndors masterDetEndors = new MasterDetEndors();
        masterDetEndors.setMsenEndorsNo(masterEndors.getMsenEndorsNo());
        masterDetEndors.setMsenfNumber(ConvertUtil.safeLong(1));
        masterDetEndors.setLsjeId(ConvertUtil.safeLong(27));
        masterDetEndors.setMsteInsuredNo(ConvertUtil.safeLong(1));
        return masterDetEndors;
    }

    private MasterEndors makeMasterEndors(CancelPolisDTO CancelPolisDTO, String noEndors, Date dateNow) {
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

    private MasterProduction makeMasterProduction(MasterPolicy masterPolicy, Date dateNow, BigDecimal kurs,
                                                  MasterBilling masterBilling, String regSpaj, BigDecimal policyCost) {
        MasterProduction masterProduction = new MasterProduction();
        masterProduction.setLstbId(masterPolicy.getLstbId());
        masterProduction.setRegSpaj(regSpaj);
        masterProduction.setMsproProdKe(2);
        masterProduction.setMsproProdDate(dateNow);
        masterProduction.setMsbiTahunKe(1);
        masterProduction.setMsbiPremiKe(1);
        masterProduction.setMsproJnProd("3");
        masterProduction.setLkuId(masterPolicy.getLkuId());
        masterProduction.setMsproNilaiKurs(kurs);//
        masterProduction.setMsproBegDate(masterPolicy.getBegDate());
        masterProduction.setMsproEndDate(masterPolicy.getEndDate());
        masterProduction.setMsproPolicyCost(policyCost);
        masterProduction.setMsproCardCost(masterBilling.getMsbiTtlCardCost());
        masterProduction.setMsproStamp(masterBilling.getMsbiStamp());
        masterProduction.setMsproJmlMember(1);
        masterProduction.setLcaId(masterPolicy.getLcaId());
        masterProduction.setLwkId(masterPolicy.getLwkId());
        masterProduction.setLsrgId(Long.valueOf(masterPolicy.getLsrgId()));
        return masterProduction;
    }

    private MasterDetProduction makeMasterDetProduction(MasterPolicy masterPolicy, Date dateNow, MasterProductInsured masterProductInsured, MasterProductSetup masterProductSetup, String regSpaj) {
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
        List<Integer> reminderTypes = Arrays.asList(StaticValue.SERTIFIKAT_DIBATALKAN_NOTIFICATION_TYPE, StaticValue.SERTIFIKAT_DIBATALKAN_NOTIFICATION_WITHOUT_EMAIL_PDF_TYPE);

        List<Object[]> resultList = masterPolicyRepository.findPolicyTobeCancelled(reminderTypes);
        List<CancelPolisDTO> policyToBeCancelledList = new ArrayList<>();
        for (Object[] row : resultList) {
            CancelPolisDTO dto = new CancelPolisDTO();
            dto.setRegSpaj(ConvertUtil.safeString(row[0]));
            policyToBeCancelledList.add(dto);
        }
        return policyToBeCancelledList;
    }
}

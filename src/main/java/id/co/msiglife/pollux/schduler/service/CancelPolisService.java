package id.co.msiglife.pollux.schduler.service;

import id.co.msiglife.pollux.schduler.dto.CancelPolisDTO;
import id.co.msiglife.pollux.schduler.entity.*;
import id.co.msiglife.pollux.schduler.entity.polis.*;
import id.co.msiglife.pollux.schduler.entity.Journal.MstPtcJm;
import id.co.msiglife.pollux.schduler.entity.Journal.MstPtcTm;
import id.co.msiglife.pollux.schduler.projection.CancelJmProjection;
import id.co.msiglife.pollux.schduler.repository.*;
import id.co.msiglife.pollux.schduler.repository.polis.*;
import id.co.msiglife.pollux.schduler.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

        for (int i = 0; i < cancelPolisDTOList.size(); i++) {
            CancelPolisDTO dto = cancelPolisDTOList.get(i);
            MasterPolicy masterPolicy = masterPolicyRepository.findByRegSpaj(dto.getRegSpaj());
            MasterBilling masterBilling = masterBillingRepository.findByRegSpaj(dto.getRegSpaj());
            MasterInsured masterInsured = masterInsuredRepository.findByRegSpajAndInsuredNo(dto.getRegSpaj(), 1);
            MasterProductInsured masterProductInsured = masterProductInsuredRepository.findMainProduct(dto.getRegSpaj(), 0, 1).get(0);
            MasterProductSetup masterProductSetup = masterProductSetupRepository.findByProductSetupId(masterPolicy.getProductId());
            MasterProduction masterProduction = masterProductionRepository.findByRegSpajAndMsproProdKe(dto.getRegSpaj(), 1);

            String noJm = ptcJmRepository.getNoJm();
            String noEndors = masterPolicyRepository.getNoEndors();

            if(!masterPolicy.getLkuId().equals("1")){
                SimpleDateFormat years = new SimpleDateFormat("yyyy");
                SimpleDateFormat months = new SimpleDateFormat("MM");
                String yearNow = years.format(dateNow);
                String monthNow = months.format(dateNow);

                kurs = ptcJmRepository.getKursLkuId02(yearNow, monthNow);
            }

            // 1) MST_ENDORS
            // for mst endors id (SELECT eka.pac_counter.GET_TAX_ADJ_ID() FROM DUAL;)
            MasterEndors masterEndors = insertMasterEndors(dto, noEndors, dateNow);
            endorses.add(masterEndors);

            // 2) MST_DET_ENDORS
            MasterDetEndors masterDetEndors = insertMasterDetEndors(dto, masterEndors, dateNow);
            detEndorses.add(masterDetEndors);

            // 3) MST_PRODUCTION
            MasterProduction masterProductionCancel = insertMasterProduction(masterPolicy, dateNow, kurs, masterBilling, dto.getRegSpaj());
            masterProductionCancel.setNoJm(noJm);
            prods.add(masterProductionCancel);

            // 4) MST_DET_PRODUCTION
            masterDetProductionRepository.copyDetProductionBySpaj(dto.getRegSpaj());

            // 5) POSITION SPAJ
            MasterPositionSPAJ masterPositionSPAJ = insertMasterPositionSpaj(dto, masterEndors, masterInsured);
            positions.add(masterPositionSPAJ);

            // 6) MST_PTC_TM (no_jm berasal dari generator mu)
            MstPtcTm mstPtcTm = insertMstTm(masterProduction, dateNow, noJm);
            tms.add(mstPtcTm);

            // 7) MST_PTC_JM_LIST (baris kas/debet/creditnya)
            List<CancelJmProjection> rows = ptcJmRepository.buildCancelJmRows(
                    noJm, masterProduction.getNoJm(), dto.getRegSpaj()
            );
            insertMstJm(rows, ptcJmRepository);

            // 9) update master Billing
            masterBilling.setMsbiNoJmBtl(noJm);
            masterBilling.setMsbiTglBatal(masterEndors.getMsenInputDate());
            masterBilling.setLspdId(ConvertUtil.safeLong(25));
            masterBilling.setMsbiActive(0);
            masterBillingList.add(masterBilling);
        }

        flushAndClear(endorses, detEndorses, positions, prods, detProds, tms, jmList, masterBillingList);

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

    private MstPtcTm insertMstTm(MasterProduction masterProduction, Date dateNow, String noJm){
        MstPtcTm mstPtcTm = new MstPtcTm();
        mstPtcTm.setNoJm(noJm);
        mstPtcTm.setNoJmRef(masterProduction.getNoJm());
        mstPtcTm.setPosition(1);
        mstPtcTm.setTglJurnal(dateNow);
        mstPtcTm.setUserInput(512);
        ptcTmRepository.save(mstPtcTm);
        return mstPtcTm;
    }

    private void insertMstJm(List<CancelJmProjection> rows, PtcJmRepository ptcJmRepository){
        rows.forEach(r -> ptcJmRepository.insertMstPtcJm(
                r.getNO_JM(),
                r.getNO_JURNAL(),
                r.getKETERANGAN(),
                r.getMJM_JUMLAH(),
                r.getMJM_CD(),
                r.getMJM_PROJECT(),
                r.getMJM_BUDGET(),
                r.getTYPE_TRX(),
                r.getKEY_JURNAL(),
                r.getLKU_ID(),
                r.getCONV_RATE(),
                r.getMJM_JUMLAH_USD(),
                r.getNO_POLIS(),
                r.getTAXNO() // pastikan ada di projection-mu
        ));
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

package id.co.msiglife.pollux.schduler.repository;

import id.co.msiglife.pollux.schduler.entity.MasterPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MasterPolicyRepository extends JpaRepository<MasterPolicy, String> {
    Long countMasterPolicyByRegSpaj(String regSpaj);
    MasterPolicy findByRegSpaj(String regSpaj);

    @Query(value = "SELECT " +
            "pol.mspo_policy_no AS Nomor_Sertfikat, " +
            "bankCabangClient.mcl_first AS Nama_Bank_Cabang, " +
            "tertanggungClient.mcl_first AS Nama_Tertanggung, " +
            "tertanggungPerson.mspe_date_birth AS Tanggal_Lahir_Tertanggung, " +
            "insured.mste_age AS Usia_Tertanggung, " +
            "pol.mspo_ins_period AS Masa_Asuransi_Tahun, " +
            "pol.mspo_ins_bulan AS Masa_Asuransi_Bulan, " +
            "hitung.premi_dibayar_nasabah AS Premi, " +
            "pol.mspo_date_print AS Tanggal_Cetak_Sertifikat, " +
            "bankPks.mcl_first AS Nama_Bank_PKS, " +
            "pks.atas_nama AS Nama_Rekening_PKS, " +
            "pks.rekening AS Nomor_Rekening_PKS, " +
            "bankCabangBranch.email_cabang AS Email_Cabang, " +
            "bankCabangBranch.email_ao AS Email_AO, " +
            "pol.reg_spaj AS Reg_SPAJ, " +
            "hitung.uang_pertanggungan AS Uang_Pertanggungan, " +
            "tertanggungPerson.mspe_email AS Email_Tertanggung " +
            "FROM eka.mst_policy pol " +
            "JOIN eka.mst_billing bill ON bill.reg_spaj = pol.reg_spaj " +
            "JOIN eka.mst_pks_cl pks ON pol.pks_id = pks.pks_id " +
            "JOIN eka.mst_insured insured ON pol.reg_spaj = insured.reg_spaj " +
            "JOIN eka.mst_client tertanggungClient ON insured.mste_insured = tertanggungClient.mcl_id " +
            "JOIN eka.mst_person tertanggungPerson ON insured.mste_insured = tertanggungPerson.mcl_id " +
            "JOIN eka.mst_client bankCabangClient ON pol.mspo_customer = bankCabangClient.mcl_id " +
            "JOIN eka.mst_branchpartner_cl bankCabangBranch ON pol.mspo_customer = bankCabangBranch.mcl_id " +
            "JOIN eka.mst_client bankPks ON pks.mcl_id = bankPks.mcl_id " +
            "JOIN eka.mst_perhitungan_premi_plx hitung ON pol.reg_spaj = hitung.reg_spaj AND hitung.rate_premi_next = 0 " +
            "JOIN eka.lst_policy_status lps on lps.lssp_id = pol.lssp_id " +
            "JOIN eka.mst_pks_reminder_days r ON pol.pks_id = r.pks_id " +
            "LEFT JOIN ( " +
            "    SELECT " +
            "      mdw.MSWF_DOC_NUMBER as reg_spaj, " +
            "      lws.LSWF_STATUS_NAME as status_name " +
            "    FROM  " +
            "      EKA.MST_WF_DOCUMENT mdw  " +
            "      INNER JOIN EKA.LST_WF_STATUS lws ON lws.LSWF_STATUS_ID = mdw.LSWF_STATUS_ID  " +
            "      AND lws.lswf_type_id = mdw.LSWF_TYPE_ID " +
            "    WHERE mdw.LSWF_TYPE_ID = 78 " +
            "  ) sts ON sts.reg_spaj = pol.reg_spaj " +
            "LEFT JOIN ( " +
            "  SELECT  " +
            "    mup.reg_spaj,  " +
            "    mup.status AS status_uw,  " +
            "    mup.final_decision AS keputusan, " +
            "    lusp.code AS status_code " +
            "  FROM EKA.MST_UNDERWRITING_PLX mup " +
            "  JOIN EKA.LST_UW_STATUS_PLX lusp ON lusp.uw_status_id = mup.status " +
            "  ) uw ON uw.reg_spaj = pol.reg_spaj " +
            "WHERE EXISTS (SELECT 1 FROM eka.mst_billing bill WHERE bill.reg_spaj = pol.reg_spaj) " +
            "AND NOT EXISTS (SELECT 1 FROM eka.mst_payment pay WHERE pay.reg_spaj = pol.reg_spaj) " +
            "AND EXISTS (SELECT 1 FROM eka.MST_AUTO_CANCEL_EMAIL_LOG WHERE reg_spaj = pol.reg_spaj AND reminder_days = r.reminder_days and flag_done = 0 and reminder_type = :reminderType) " +
            "AND (lower(lps.lssp_status) = 'inforce' OR lower(uw.status_uw) = 'inforce' OR lower(sts.status_name) = 'inforce') " +
            "AND (pol.lssp_id != 8) " +
            "AND LOWER(lps.lssp_status) NOT LIKE '%cancel%' " +
            "AND (pol.lssp_id != 12 AND pol.lspd_id != 999) " +
            "ORDER BY pol.mspo_date_print DESC",
            nativeQuery = true)
    List<Object[]> findAutoCancelByReminderType(@Param("reminderType") Integer reminderType);

    @Query(value = "SELECT pol.reg_spaj AS reg_spaj, r.reminder_days as reminderDays, r.reminder_type as reminderType " +
            "FROM eka.mst_policy pol " +
            "JOIN eka.mst_billing bill ON bill.reg_spaj = pol.reg_spaj " +
            "JOIN eka.mst_underwriting_plx uw ON pol.reg_spaj = uw.reg_spaj " +
            "JOIN eka.mst_pks_reminder_days r ON pol.pks_id = r.pks_id " +
            "LEFT JOIN ( " +
            "   SELECT mdw.MSWF_DOC_NUMBER as reg_spaj, lws.LSWF_STATUS_NAME as status_name " +
            "   FROM EKA.MST_WF_DOCUMENT mdw " +
            "   INNER JOIN EKA.LST_WF_STATUS lws " +
            "     ON lws.LSWF_STATUS_ID = mdw.LSWF_STATUS_ID " +
            "    AND lws.lswf_type_id = mdw.LSWF_TYPE_ID " +
            "WHERE mdw.LSWF_TYPE_ID = 78 " +
            ") sts ON sts.reg_spaj = pol.reg_spaj " +
            "LEFT JOIN ( " +
            "   SELECT mup.reg_spaj, mup.status AS status_uw, mup.final_decision AS keputusan, lusp.code AS status_code " +
            "   FROM EKA.MST_UNDERWRITING_PLX mup " +
            "   JOIN EKA.LST_UW_STATUS_PLX lusp ON lusp.uw_status_id = mup.status " +
            ") uw ON uw.reg_spaj = pol.reg_spaj " +
            "LEFT JOIN eka.lst_policy_status lps ON lps.lssp_id = pol.lssp_id " +
            "WHERE r.reminder_type != 5 " +
            "AND TRUNC(SYSDATE) = TRUNC(pol.mspo_date_print + r.reminder_days) " +
            "AND EXISTS (SELECT 1 FROM eka.mst_billing b2 WHERE b2.reg_spaj = pol.reg_spaj) " +
            "AND NOT EXISTS (SELECT 1 FROM eka.mst_payment pay WHERE pay.reg_spaj = pol.reg_spaj) " +
            "AND NOT EXISTS (SELECT 1 FROM eka.MST_AUTO_CANCEL_EMAIL_LOG log " +
            "                WHERE log.reg_spaj = pol.reg_spaj " +
            "                AND log.reminder_days = r.reminder_days) " +
            "AND (LOWER(lps.lssp_status) = 'inforce' " +
            "     OR LOWER(uw.status_uw) = 'inforce' " +
            "     OR LOWER(sts.status_name) = 'inforce') " +
            "AND (pol.lssp_id != 8) " +
            "AND LOWER(lps.lssp_status) NOT LIKE '%cancel%' " +
            "AND (pol.lssp_id != 12 AND pol.lspd_id != 999) " +
            "ORDER BY pol.mspo_date_print DESC",
            nativeQuery = true)
    List<Object[]> findAllSuratAutoCancel();

    @Query(value = "SELECT pol.reg_spaj AS reg_spaj, r.reminder_days as reminderDays, r.reminder_type as reminderType " +
            "FROM " +
            "EKA.MST_POLICY pol " +
            "JOIN EKA.MST_POLICY_SIGNDATE sign ON pol.REG_SPAJ = sign.REG_SPAJ " +
            "JOIN eka.mst_pks_reminder_days r ON pol.pks_id = r.pks_id " +
            "LEFT JOIN eka.lst_policy_status lps ON lps.lssp_id = pol.lssp_id " +
            "LEFT JOIN (" +
            "    SELECT " +
            "      mdw.MSWF_DOC_NUMBER as reg_spaj, " +
            "      lws.LSWF_STATUS_NAME as status_name " +
            "    FROM  " +
            "      EKA.MST_WF_DOCUMENT mdw  " +
            "      INNER JOIN EKA.LST_WF_STATUS lws ON lws.LSWF_STATUS_ID = mdw.LSWF_STATUS_ID  " +
            "      AND lws.lswf_type_id = mdw.LSWF_TYPE_ID " +
            "    WHERE mdw.LSWF_TYPE_ID = 78 " +
            "  ) sts ON sts.reg_spaj = pol.reg_spaj " +
            "LEFT JOIN ( " +
            "  SELECT  " +
            "    mup.reg_spaj,  " +
            "    mup.status AS status_uw,  " +
            "    mup.final_decision AS keputusan, " +
            "    lusp.code AS status_code " +
            "  FROM EKA.MST_UNDERWRITING_PLX mup " +
            "  JOIN EKA.LST_UW_STATUS_PLX lusp ON lusp.uw_status_id = mup.status " +
            "  ) uw ON uw.reg_spaj = pol.reg_spaj " +
            "WHERE r.reminder_type = 5 AND " +
            "TRUNC(SYSDATE) = TRUNC(sign.MSSD_SIGNDATE + r.reminder_days) AND " +
            "(pol.LSSP_ID != 1 OR pol.MSPO_POLICY_NO IS NOT NULL) AND " +
            "(pol.lssp_id != 8) AND " +
            "LOWER(lps.lssp_status) NOT LIKE '%cancel%' AND " +
            "(pol.lssp_id != 12 AND pol.lspd_id != 999) AND " +
            "(pol.LSSP_ID != 25 AND pol.LSSP_ID != 27) " +
            "AND NOT EXISTS (SELECT 1 FROM eka.MST_AUTO_CANCEL_EMAIL_LOG log " +
            "    WHERE log.reg_spaj = pol.reg_spaj " +
            "    AND log.reminder_days = r.reminder_days " +
            "    AND log.reminder_type = 5) " +
            "AND NOT \n" +
            "(LOWER(lps.lssp_status) = 'inforce' \n" +
            "   OR LOWER(uw.status_uw) = 'inforce' \n" +
            "   OR LOWER(sts.status_name) = 'inforce') " +
            "AND NOT \n" +
            "(LOWER(lps.lssp_status) LIKE 'decline%'\n" +
            "    OR LOWER(uw.status_uw) LIKE 'decline%'\n" +
            "    OR LOWER(sts.status_name) LIKE 'decline%')\n" +
            "AND NOT \n" +
            "(LOWER(lps.lssp_status) LIKE 'postpone%'\n" +
            "    OR LOWER(uw.status_uw) LIKE 'postpone%'\n" +
            "    OR LOWER(sts.status_name) LIKE 'postpone%')\n" +
            "AND LOWER(uw.keputusan) NOT LIKE 'decline%'\n" +
            "AND LOWER(uw.keputusan) NOT LIKE 'postpone%'", nativeQuery = true)
    List<Object[]> findAllPolicyTobeCancelled();

    @Query(value = "SELECT reg_spaj FROM eka.MST_AUTO_CANCEL_EMAIL_LOG WHERE reminder_type = 5 AND flag_done = 0", nativeQuery = true)
    List<Object[]> findSpajkTobeCancelled();

    @Query(value = "SELECT reg_spaj FROM eka.MST_AUTO_CANCEL_EMAIL_LOG WHERE reminder_type IN (:reminderTypes) AND flag_done = 1 AND FLAG_POLIS_CANCELED = 0", nativeQuery = true)
    List<Object[]> findPolicyTobeCancelled(@Param("reminderTypes") List<Integer> reminderTypes);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE EKA.MST_POLICY  " +
            "      SET LSSP_ID = 8   " +
            "      WHERE ( EKA.MST_POLICY.REG_SPAJ IN (:regSpaj) ) AND  " +
            "        ( EKA.MST_POLICY.LSTB_ID = 2 )",
            nativeQuery = true)
    void updatePolisCanceled(@Param("regSpaj") List<String> regSpaj);

    @Query(value =
            "SELECT eka.pac_counter.Get_no_endors_seq() FROM DUAL",
            nativeQuery = true)
    String getNoEndors();


}

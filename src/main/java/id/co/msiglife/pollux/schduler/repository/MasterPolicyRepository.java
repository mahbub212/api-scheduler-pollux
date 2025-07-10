package id.co.msiglife.pollux.schduler.repository;

import id.co.msiglife.pollux.schduler.entity.MasterPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
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
            "uw.premium AS Premi, " +
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
            "JOIN eka.mst_underwriting_plx uw ON pol.reg_spaj = uw.reg_spaj " +
            "JOIN eka.mst_billing bill ON bill.reg_spaj = pol.reg_spaj " +
            "JOIN eka.mst_pks_cl pks ON pol.pks_id = pks.pks_id " +
            "JOIN eka.mst_insured insured ON pol.reg_spaj = insured.reg_spaj " +
            "JOIN eka.mst_client tertanggungClient ON insured.mste_insured = tertanggungClient.mcl_id " +
            "JOIN eka.mst_person tertanggungPerson ON insured.mste_insured = tertanggungPerson.mcl_id " +
            "JOIN eka.mst_client bankCabangClient ON pol.mspo_customer = bankCabangClient.mcl_id " +
            "JOIN eka.mst_branchpartner_cl bankCabangBranch ON pol.mspo_customer = bankCabangBranch.mcl_id " +
            "JOIN eka.mst_client bankPks ON pks.mcl_id = bankPks.mcl_id " +
            "JOIN eka.mst_perhitungan_premi_plx hitung ON pol.reg_spaj = hitung.reg_spaj AND hitung.rate_premi_next = 0 " +
            "WHERE TRUNC(SYSDATE) = TRUNC(pol.mspo_date_print + :days) " +
            "AND EXISTS (SELECT 1 FROM eka.mst_billing bill WHERE bill.reg_spaj = pol.reg_spaj) " +
            "AND NOT EXISTS (SELECT 1 FROM eka.mst_payment pay WHERE pay.reg_spaj = pol.reg_spaj) " +
            "ORDER BY pol.mspo_date_print DESC",
            nativeQuery = true)
    List<Object[]> findAutoCancelByDays(@Param("days") Integer days);

}

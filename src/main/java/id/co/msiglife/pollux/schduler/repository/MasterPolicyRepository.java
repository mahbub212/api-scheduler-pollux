package id.co.msiglife.pollux.schduler.repository;

import id.co.msiglife.pollux.schduler.entity.MasterPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MasterPolicyRepository extends JpaRepository<MasterPolicy, String> {
    Long countMasterPolicyByRegSpaj(String regSpaj);
    MasterPolicy findByRegSpaj(String regSpaj);

    @Query("SELECT " +
            "pol.mspo_policy_no AS \"Nomor Sertfikat\", " +
            "bankCabangClient.mcl_first AS \"Nama Bank Cabang\", " +
            "tertanggungClient.mcl_first AS \"Nama Tertanggung\", " +
            "tertanggungPerson.mspe_date_birth AS \"Tanggal Lahir Tertanggung\", " +
            "insured.mste_age AS \"Usia Tertanggung\", " +
            "pol.mspo_ins_period AS \"Masa Asuransi Tahun\", " +
            "pol.mspo_ins_bulan AS \"Masa Asuransi Bulan\", " +
            "uw.premium AS \"Premi\", " +
            "pol.mspo_date_print AS \"Tanggal Cetak Sertifikat\", " +
            "bankPks.mcl_first AS \"Nama Bank PKS\", " +
            "pks.atas_nama AS \"Nama Rekening PKS\", " +
            "pks.rekening AS \"Nomor Rekening PKS\", " +
            "bankCabangBranch.email_cabang AS \"Email Cabang\", " +
            "bankCabangBranch.email_ao AS \"Email AO\", " +
            "pol.reg_spaj AS \"Reg SPAJ\", " +
            "hitung.uang_pertanggungan AS \"Uang Pertanggungan\" " +
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
            "JOIN eka.mst_perhitungan_premi_plx hitung ON pol.reg_spaj = hitung.reg_spaj " +
            "WHERE TRUNC(SYSDATE) = TRUNC(pol.mspo_date_print + :days) " +
            "AND EXISTS (SELECT 1 FROM eka.mst_billing bill WHERE bill.reg_spaj = pol.reg_spaj) " +
            "AND NOT EXISTS (SELECT 1 FROM eka.mst_payment pay WHERE pay.reg_spaj = pol.reg_spaj) " +
            "ORDER BY pol.mspo_date_print DESC")
    List<Object[]> findAutoCancelByDays(Integer days);
}

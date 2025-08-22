package id.co.msiglife.pollux.schduler.repository.CancelPolis;

import id.co.msiglife.pollux.schduler.entity.CancelPolis.MasterBilling;
import id.co.msiglife.pollux.schduler.entity.CancelPolis.MasterDetBilling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface MasterBillingRepository extends JpaRepository<MasterBilling, String> {
    MasterBilling findByRegSpaj(String regSpaj);

    @Query(value =
            "SELECT COALESCE(SUM(COALESCE(MSBI_POLICY_COST,0) + COALESCE(MSBI_STAMP,0)), 0) " +
                    "FROM eka.mst_billing " +
                    "WHERE reg_spaj = :regSpaj " +
                    "AND msbi_tahun_ke = 1 " +
                    "AND msbi_premi_ke = 1",
            nativeQuery = true)
    BigDecimal findAdmByRegSpaj(@Param("regSpaj") String regSpaj);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value =
            "UPDATE EKA.MST_BILLING " +
                    "SET MSBI_NO_JM = :noJm " +
                    "WHERE (EKA.MST_BILLING.REG_SPAJ = :regSpaj )",
            nativeQuery = true)
    int updateJm(@Param("noJm") String noJm, @Param("regSpaj") String regSpaj);

    @Query(value =
            "SELECT count(msbi_premi_ke) " +
                    "FROM eka.mst_billing " +
                    "WHERE reg_spaj = :regSpaj " +
                    "AND msbi_active  = 1 ",
            nativeQuery = true)
    BigDecimal findTotalBill(@Param("regSpaj") String regSpaj);
}

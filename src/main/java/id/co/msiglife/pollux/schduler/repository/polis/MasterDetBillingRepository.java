package id.co.msiglife.pollux.schduler.repository.polis;

import id.co.msiglife.pollux.schduler.entity.polis.MasterDetBilling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface MasterDetBillingRepository extends JpaRepository<MasterDetBilling, Long> {
    MasterDetBilling findByRegSpaj(String regSpaj);

    @Query(value =
            "SELECT MAX(EKA.MST_PRE_BILLING.MSPBI_NUMBER) \n" +
                    "FROM EKA.MST_PRE_BILLING \n" +
                    "WHERE  ( EKA.MST_PRE_BILLING.REG_SPAJ = :is_reg_spaj ) AND  \n" +
                    "       ( EKA.MST_PRE_BILLING.MSPBI_ACTIVE = 1 )",
            nativeQuery = true)
    Integer countCicilan (@Param("is_reg_spaj") String regSpaj);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO EKA.MST_DET_BILLING\n" +
            "            (REG_SPAJ,\n" +
            "             MSBI_TAHUN_KE,\n" +
            "             MSBI_PREMI_KE,\n" +
            "             MSDB_DET_KE,\n" +
            "             LSBS_ID,\n" +
            "             LSDBS_NUMBER,\n" +
            "             MSDB_PREMIUM,\n" +
            "             MSDB_DISCOUNT)\n" +
            "        SELECT\n" +
            "            REG_SPAJ,\n" +
            "            :tahunKe,\n" +
            "            :premiKe,\n" +
            "            ROWNUM,\n" +
            "            LSBS_ID,\n" +
            "            LSDBS_NUMBER,\n" +
            "            ROUND(MSPR_PREMIUM / :cntCicilan, 2),\n" +
            "            NVL(ROUND(MSPR_DISCOUNT / :cntCicilan, 2), 0)\n" +
            "        FROM EKA.MST_PRODUCT_INSURED\n" +
            "        WHERE REG_SPAJ = :regSpaj\n" +
            "          AND MSTE_INSURED_NO = 1\n" +
            "          AND MSPR_ACTIVE = 1", nativeQuery = true)
    void insertFromProductInsuredWithCicilan(@Param("regSpaj") String regSpaj,
                                  @Param("tahunKe") int tahunKe,
                                  @Param("premiKe") int premiKe,
                                  @Param("cntCicilan") int cntCicilan);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO EKA.MST_DET_BILLING\n" +
            "            (REG_SPAJ,\n" +
            "             MSBI_TAHUN_KE,\n" +
            "             MSBI_PREMI_KE,\n" +
            "             MSDB_DET_KE,\n" +
            "             LSBS_ID,\n" +
            "             LSDBS_NUMBER,\n" +
            "             MSDB_PREMIUM,\n" +
            "             MSDB_DISCOUNT)\n" +
            "        SELECT \n" +
            "            MPI.REG_SPAJ,\n" +
            "            :tahunKe,\n" +
            "            :premiKe,\n" +
            "            ROWNUM,\n" +
            "            MPI.LSBS_ID,\n" +
            "            MPI.LSDBS_NUMBER,\n" +
            "            SUM(MPI.MSPR_PREMIUM),\n" +
            "            NVL(SUM(MPI.MSPR_DISCOUNT), 0)\n" +
            "        FROM EKA.MST_PRODUCT_INSURED MPI\n" +
            "        WHERE MPI.REG_SPAJ = :regSpaj\n" +
            "          AND MPI.MSPR_ACTIVE = 1\n" +
            "          AND MPI.MSPR_PREMIUM > 0\n" +
            "        GROUP BY MPI.REG_SPAJ, \n" +
            "                 MPI.LSBS_ID, \n" +
            "                 MPI.LSDBS_NUMBER,\n" +
            "                 :tahunKe,\n" +
            "                 :premiKe,\n" +
            "                 ROWNUM", nativeQuery = true)
    void insertFromProductInsuredWithNoCicilan(@Param("regSpaj") String regSpaj,
                                         @Param("tahunKe") int tahunKe,
                                         @Param("premiKe") int premiKe);
}


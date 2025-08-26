package id.co.msiglife.pollux.schduler.repository.polis;

import id.co.msiglife.pollux.schduler.entity.polis.MasterDetProduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface MasterDetProductionRepository extends JpaRepository<MasterDetProduction, String> {
    MasterDetProduction findByRegSpaj(String spaj);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query(value = "INSERT INTO EKA.MST_DET_PRODUCTION " +
            "(LSTB_ID, REG_SPAJ, MSPRO_PROD_KE, MSPRO_PROD_DATE, LSBS_ID, LSDBS_NUMBER, MSBI_TAHUN_KE, MSBI_PREMI_KE, MSDPR_PREMIUM, MSDPR_DISCOUNT, MSDPR_TSI, MSDPR_URUT) " +
                    "SELECT " +
             "LSTB_ID, REG_SPAJ, 2, TRUNC(SYSDATE), LSBS_ID, LSDBS_NUMBER, MSBI_TAHUN_KE, MSBI_PREMI_KE, MSDPR_PREMIUM, MSDPR_DISCOUNT, MSDPR_TSI, MSDPR_URUT " +
             "FROM EKA.MST_DET_PRODUCTION " +
             "WHERE REG_SPAJ = :regSpaj ", nativeQuery = true)
    int copyDetProductionBySpaj(@Param("regSpaj") String regSpaj);
}

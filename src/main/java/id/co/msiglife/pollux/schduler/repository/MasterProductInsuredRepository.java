package id.co.msiglife.pollux.schduler.repository;

import id.co.msiglife.pollux.schduler.entity.MasterProductInsured;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MasterProductInsuredRepository extends JpaRepository<MasterProductInsured, String> {

    @Query("SELECT ins " +
            "FROM MasterProductInsured ins " +
            "JOIN MasterPolicy pol ON pol.regSpaj = ins.regSpaj " +
            "WHERE ins.regSpaj = :regSpaj AND " +
            "pol.jointLife = :flagJointLife AND " +
            "ins.msteInsuredNo = :msteInsuredNo AND " +
            "ins.lsbsId BETWEEN 300 AND 599 " +
            "ORDER by ins.urut DESC")
    List<MasterProductInsured> findMainProduct(String regSpaj, Integer flagJointLife, Integer msteInsuredNo);

    @Query(value = "SELECT " +
            "    pi.reg_spaj " +
            "    FROM EKA.MST_PRODUCT_INSURED pi " +
            "    LEFT JOIN EKA.MST_PRODSETUP_CL mpc ON pi.lsbs_id = mpc.lsbs_id AND pi.lsdbs_number = mpc.lsdbs_number " +
            "    LEFT JOIN EKA.LST_DET_BISNIS ldb ON pi.lsbs_id = ldb.lsbs_id AND pi.lsdbs_number = ldb.lsdbs_number " +
            "    WHERE pi.reg_spaj = :regSpaj and (pi.lsbs_id BETWEEN 300 AND 599 OR pi.lsbs_id >= 900)",
    nativeQuery = true)
    String checkLiniBisnis(@Param("regSpaj") String regSpaj);
}

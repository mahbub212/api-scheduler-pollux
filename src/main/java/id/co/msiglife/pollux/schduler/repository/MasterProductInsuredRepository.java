package id.co.msiglife.pollux.schduler.repository;

import id.co.msiglife.pollux.schduler.entity.MasterProductInsured;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}

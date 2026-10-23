package id.co.msiglife.pollux.schduler.repository;

import id.co.msiglife.pollux.schduler.entity.MasterBranchPartner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MasterBranchPartnerRepository extends JpaRepository<MasterBranchPartner, String> {
    MasterBranchPartner findByPksId(String pksId);
    MasterBranchPartner findByMclId(String mclId);
    List<MasterBranchPartner> findAllByMclIdIn(List<String> mclId);

    @Query("SELECT branch.mclId, branch.msagId, " +
            "(client.first || client.middle || client.last) as name, " +
            "branch.aoName as agentName, " +
            "(agent.lcaId || agent.lwkId || agent.lsrgId) as regionCode, " +
            "(cabang.nama || ' / ' || wakil.nama || ' / ' || region.nama) as regionName " +
            "FROM MasterBranchPartner branch " +
            "JOIN MasterClient client ON branch.mclId = client.mclId " +
            "JOIN MasterAgent agent ON agent.msagId = branch.msagId " +
            "JOIN MasterClient agentClient ON agentClient.mclId = agent.mclId " +
            "JOIN ListCabang cabang ON cabang.lcaId = agent.lcaId " +
            "JOIN ListWakil wakil ON wakil.lcaId = agent.lcaId AND wakil.lwkId = agent.lwkId " +
            "JOIN ListRegion region ON region.lcaId = agent.lcaId AND region.lwkId = agent.lwkId AND region.lsrgId = agent.lsrgId " +
            "WHERE branch.pksId = :pksId AND branch.flagAktif = 1")
    Page<Object[]> findByPksIdAndFlagAktif(String pksId, Pageable pageable);

    @Query(value = "SELECT * FROM EKA.MST_BRANCHPARTNER_CL WHERE MCL_ID IN (:param)",
            nativeQuery = true)
    List<MasterBranchPartner> findByMclIdBulk(@Param("param") List<String> param);

    @Query(value = "SELECT mdc_bank_email FROM eka.mst_det_customer_group_mri where mdc_mcl_id = :param", nativeQuery = true)
    String findByMclIdEmail(@Param("param") String param);

    @Query(value = "SELECT mdc_ao_email, mdc_bank_email FROM eka.mst_det_customer_group_mri where mdc_mcl_id = :param", nativeQuery = true)
    List<Object[]> findByMclIdAoAndBankEmail(@Param("param") String param);
}

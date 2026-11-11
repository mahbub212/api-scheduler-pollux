package id.co.msiglife.pollux.schduler.repository;

import id.co.msiglife.pollux.schduler.entity.MasterPKS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MasterPKSRepository extends JpaRepository<MasterPKS, String> {
    MasterPKS findFirstByNoPksAndFlagAktif(String noPks, Integer flagAktif);

    MasterPKS findByNoPks(String noPks);

    MasterPKS findByPksId(String pksId);

    List<MasterPKS> findAllByPksIdIn(List<String> pksId);

    List<MasterPKS> findByLssbKode(Integer lssbKode);
   
    List<MasterPKS> findByFlagAktif(Integer active);

    Page<MasterPKS> findByLssbKodeAndFlagAktif(Integer lssbKode, Integer flagAktif, Pageable pageable);

    Page<MasterPKS> findAll(Specification<MasterPKS> specification, Pageable pageable);

    @Query(value = "select * from EKA.MST_PKS_CL where PKS_ID in (:regSpajk)", nativeQuery = true)
    List<MasterPKS> findByNoPksBulk(List<String> regSpajk);

    @Query(value = "SELECT mcl_id FROM eka.mst_mou WHERE mpi_mou = :mpiMou", nativeQuery = true)
    String findMclIdMou(@Param("mpiMou") String mpiMou);

    @Query(value =
            "SELECT NVL(COUNT(A.PKS_ID), 0) " +
                    "FROM EKA.MST_PKS_CL A " +
                    "WHERE EXISTS ( " +
                    "    SELECT 1 " +
                    "    FROM EKA.MST_CONFIG B " +
                    "    WHERE B.MC_ID = '590' " +
                    "    AND REGEXP_LIKE( " +
                    "        B.NAME, " +
                    "        '(^|,)[[:space:]]*' || A.PKS_ID || '[[:space:]]*(,|$)' " +
                    "    ) " +
                    "    AND A.PKS_ID = :pksId " +
                    ")",
            nativeQuery = true)
    Integer searchdByPksId(@Param("pksId") String pksId);

}

package id.co.msiglife.pollux.schduler.repository;

import id.co.msiglife.pollux.schduler.entity.MasterUnderwriting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterUnderwritingRepository extends JpaRepository<MasterUnderwriting, String> {
    @Query(value = "select * from EKA.MST_UNDERWRITING_PLX where REG_SPAJ = :regSpajk", nativeQuery = true)
    MasterUnderwriting findbyRegSpajk(String regSpajk);
}

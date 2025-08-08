package id.co.msiglife.pollux.schduler.repository;

import id.co.msiglife.pollux.schduler.entity.BcaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MappingBcaRepository extends JpaRepository<BcaData, String> {

    @Query(value = "select * from eka.mst_ordercl_api " +
            "where reg_spaj = :regSpaj and tipe_trans = :tipeTrans order by no_urut desc fetch first 1 row only", nativeQuery = true)
    Optional<BcaData> findByRegSpajAndTipeTrans(String regSpaj, Integer tipeTrans);
}

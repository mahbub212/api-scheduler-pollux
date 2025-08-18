package id.co.msiglife.pollux.schduler.repository;

import id.co.msiglife.pollux.schduler.entity.BcaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface MappingBcaRepository extends JpaRepository<BcaData, String> {

    @Query(value = "select * from eka.mst_ordercl_api " +
            "where reg_spaj = :regSpaj and tipe_trans = :tipeTrans order by no_urut desc fetch first 1 row only", nativeQuery = true)
    Optional<BcaData> findByRegSpajAndTipeTrans(String regSpaj, Integer tipeTrans);

    @Query(value = "select * from eka.mst_ordercl_api " +
                    "where order_status = 0", nativeQuery = true)
    List<BcaData> getDataTakeoutPosponedDecline();

    @Query(value = "select * from eka.mst_ordercl_api " +
            "where reg_spaj = :regSpaj", nativeQuery = true)
    List<BcaData> getDataForUpdateOrderStatus(String regSpaj);

    @Modifying
    @Transactional
    @Query("UPDATE BcaData b " +
            "SET b.orderStatus = :orderStatusTo " +
            "WHERE b.regSpaj = :regSpaj and b.orderStatus = :orderStatus")
    Integer updateBcaDataOrderStatusAndUserByRegSpaj(
            @Param("regSpaj") String regSpaj,
            @Param("orderStatus") Integer orderStatus,
            @Param("orderStatusTo") Integer orderStatusTo
    );
}

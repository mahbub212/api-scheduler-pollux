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

    @Query(value = "     select moa.* from eka.mst_ordercl_api moa\n" +
            "     join eka.mst_underwriting_plx mup on mup.reg_spaj = moa.reg_spaj\n" +
            "     join eka.mst_wf_document mwd on mwd.mswf_doc_number = moa.reg_spaj\n" +
            "     where moa.order_status = 0 and mup.final_decision in ('Postpone', 'Decline') and mwd.lswf_state_id in (-3, -2) and moa.tipe_trans = 3", nativeQuery = true)
    List<BcaData> getDataTakeoutCancel();

    @Query(value = "     select moa.* from eka.mst_ordercl_api moa\n" +
            "     join eka.mst_underwriting_plx mup on mup.reg_spaj = moa.reg_spaj\n" +
            "     join eka.mst_wf_document mwd on mwd.mswf_doc_number = moa.reg_spaj\n" +
            "     where moa.order_status = 1 and mup.final_decision in ('Postpone', 'Decline') and mwd.lswf_state_id in (-3, -2) and moa.tipe_trans = 1", nativeQuery = true)
    List<BcaData> getDataTakeoutOrder();

    @Query(value = "select * from eka.mst_ordercl_api " +
            "where reg_spaj = :regSpaj", nativeQuery = true)
    List<BcaData> getDataBcaByRegSpaj(String regSpaj);

    @Modifying
    @Transactional
    @Query("UPDATE BcaData b " +
            "SET b.orderStatus = :orderStatusTo, b.orderUserProcess = :userId, b.orderProcessDate = CURRENT_TIMESTAMP " +
            "WHERE b.regSpaj in :regSpajList AND b.tipeTrans = :tipeTrx " +
            " AND b.orderStatus = :orderStatus")
    Integer updateBcaDataOrderStatusAndUserByRegSpajAndTipeTransAndOrderStatus(
            @Param("regSpajList") List<String> regSpaj,
            @Param("orderStatus") Integer orderStatus,
            @Param("tipeTrx") Integer tipeTrx,
            @Param("orderStatusTo") Integer orderStatusTo,
            @Param("userId") Integer userId
    );

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

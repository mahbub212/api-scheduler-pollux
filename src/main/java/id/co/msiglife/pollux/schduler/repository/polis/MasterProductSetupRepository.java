package id.co.msiglife.pollux.schduler.repository.polis;

import id.co.msiglife.pollux.schduler.entity.polis.MasterProductSetup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MasterProductSetupRepository extends JpaRepository<MasterProductSetup, String> {
    MasterProductSetup findByProductSetupId(String productSetupId);

    Page<MasterProductSetup> findAll(Specification<MasterProductSetup> specification, Pageable pageable);

    @Query("SELECT product.productSetupId, product.productId, product.productNumber, bisnis.productName, product.interest, product.flagUsia " +
            "FROM MasterProductSetup product " +
            "LEFT JOIN ProductListDet bisnis " +
            "ON bisnis.productId = product.productId and bisnis.productNumber = product.productNumber " +
            "WHERE product.pksId = :pksId AND product.flagAktif = 1")
    Page<Object[]> findByPksIdAndFlagAktif(String pksId, Pageable pageable);

    @Query(value =
            "select EKA.MST_PRODSETUP_CL.TIPE_JURNAL " +
            " from eka.MST_PRODSETUP_CL  " +
            "where EKA.MST_PRODSETUP_CL.PKS_ID   = :ls_pks_id and " +
            "EKA.MST_PRODSETUP_CL.LSBS_ID = :ll_lsbs and " +
            "EKA.MST_PRODSETUP_CL.LSDBS_NUMBER = :ll_lsdbs",
            nativeQuery = true)
    String findTipeJournal(@Param("ls_pks_id") String pksId,
                                @Param("ll_lsbs") int lsbsId,
                                @Param("ll_lsdbs") int lsdbsNumber);
}

package id.co.msiglife.pollux.schduler.repository;

import id.co.msiglife.pollux.schduler.composite.CompListPromotionPackage;
import id.co.msiglife.pollux.schduler.entity.ListPromotionPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ListPromotionPackageRepository extends JpaRepository<ListPromotionPackage, CompListPromotionPackage> {

    @Query(value = "select * from EKA.LST_PROMOTION_PACKAGE " +
            "where reference_no = :referenceNo and flag_selected = 1 order by no_urut desc fetch first 1 row only", nativeQuery = true)
    ListPromotionPackage findByReferenceNoFirstOnly(String referenceNo);
}

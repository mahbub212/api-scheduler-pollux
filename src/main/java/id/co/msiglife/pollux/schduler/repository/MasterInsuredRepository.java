package id.co.msiglife.pollux.schduler.repository;

import id.co.msiglife.pollux.schduler.entity.MasterInsured;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterInsuredRepository extends JpaRepository<MasterInsured, String> {
    @Query(value = "SELECT insured FROM MasterInsured insured WHERE insured.regSpaj = :regSpaj AND insured.insuredNo = :insuredNo")
    MasterInsured findByRegSpajAndInsuredNo(String regSpaj, Integer insuredNo);
}

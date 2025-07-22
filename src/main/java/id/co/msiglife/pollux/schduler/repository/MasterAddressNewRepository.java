package id.co.msiglife.pollux.schduler.repository;

import id.co.msiglife.pollux.schduler.entity.MasterAddressNew;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterAddressNewRepository extends JpaRepository<MasterAddressNew, String> {
    MasterAddressNew findByMclId(String mclId);
}

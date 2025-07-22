package id.co.msiglife.pollux.schduler.repository;

import id.co.msiglife.pollux.schduler.entity.MasterClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterClientRepository extends JpaRepository<MasterClient, Integer> {
    MasterClient findByMclId(String mclId);
}

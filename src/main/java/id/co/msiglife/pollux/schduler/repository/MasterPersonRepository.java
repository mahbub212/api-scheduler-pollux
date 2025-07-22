package id.co.msiglife.pollux.schduler.repository;

import id.co.msiglife.pollux.schduler.entity.MasterPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterPersonRepository extends JpaRepository<MasterPerson, String> {
    MasterPerson findByMclId(String mclId);
}

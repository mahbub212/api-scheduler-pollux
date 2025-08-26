package id.co.msiglife.pollux.schduler.repository.polis;

import id.co.msiglife.pollux.schduler.entity.polis.MasterDetEndors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterDetEndorsRepository extends JpaRepository<MasterDetEndors, String> {
	
	MasterDetEndors findByMsenEndorsNo(String msenEndorsNo);
}
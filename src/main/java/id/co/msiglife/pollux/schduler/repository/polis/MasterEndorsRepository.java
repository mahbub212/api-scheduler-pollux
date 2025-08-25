package id.co.msiglife.pollux.schduler.repository.polis;

import id.co.msiglife.pollux.schduler.entity.CancelPolis.MasterEndors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterEndorsRepository extends JpaRepository<MasterEndors, String> {
	MasterEndors findByRegSpaj(String regSpaj);
}
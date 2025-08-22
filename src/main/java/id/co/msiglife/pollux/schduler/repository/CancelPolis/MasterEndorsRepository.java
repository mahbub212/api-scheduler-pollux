package id.co.msiglife.pollux.schduler.repository.CancelPolis;

import id.co.msiglife.pollux.schduler.entity.CancelPolis.MasterEndors;
import id.co.msiglife.pollux.schduler.entity.CancelPolis.MasterProduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface MasterEndorsRepository extends JpaRepository<MasterEndors, String> {
	MasterEndors findByRegSpaj(String regSpaj);
}
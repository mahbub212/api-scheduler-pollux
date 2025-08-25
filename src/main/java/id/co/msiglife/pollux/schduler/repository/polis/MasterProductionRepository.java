package id.co.msiglife.pollux.schduler.repository.polis;

import id.co.msiglife.pollux.schduler.entity.CancelPolis.MasterProduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterProductionRepository extends JpaRepository<MasterProduction, String> {
	
	MasterProduction findByRegSpaj(String regSpaj);
	MasterProduction findByRegSpajAndMsproProdKe(String regSpaj, Integer msproProdKe);
}
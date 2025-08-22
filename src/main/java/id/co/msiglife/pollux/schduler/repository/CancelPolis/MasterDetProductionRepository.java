package id.co.msiglife.pollux.schduler.repository.CancelPolis;

import id.co.msiglife.pollux.schduler.entity.CancelPolis.MasterDetProduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterDetProductionRepository extends JpaRepository<MasterDetProduction, String> {
    MasterDetProduction findByRegSpaj(String spaj);
}

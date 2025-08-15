package id.co.msiglife.pollux.schduler.repository;

import id.co.msiglife.pollux.schduler.entity.ListNegara;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListNegaraRepository extends JpaRepository<ListNegara, String> {
    ListNegara findByLsneId(Integer lsneId);
}

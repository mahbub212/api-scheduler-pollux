package id.co.msiglife.pollux.schduler.repository;

import id.co.msiglife.pollux.schduler.entity.MasterEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterEmailRepository extends JpaRepository<MasterEmail, String> {
}

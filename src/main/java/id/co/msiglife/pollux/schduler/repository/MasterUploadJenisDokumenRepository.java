package id.co.msiglife.pollux.schduler.repository;

import id.co.msiglife.pollux.schduler.entity.MasterUploadJenisDokumen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterUploadJenisDokumenRepository extends JpaRepository<MasterUploadJenisDokumen, String> {
}

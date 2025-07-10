package id.co.msiglife.pollux.schduler.repository;

import id.co.msiglife.pollux.schduler.entity.MasterAutoCancelEmailLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MasterAutoCancelEmailLogRepository  extends JpaRepository<MasterAutoCancelEmailLog, String> {
}

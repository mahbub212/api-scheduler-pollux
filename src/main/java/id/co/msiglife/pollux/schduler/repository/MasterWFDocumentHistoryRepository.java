package id.co.msiglife.pollux.schduler.repository;

import id.co.msiglife.pollux.schduler.entity.MasterWFDocumentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterWFDocumentHistoryRepository extends JpaRepository<MasterWFDocumentHistory, String> {
    Optional<MasterWFDocumentHistory> findFirstByDocIdAndLswfTypeIdAndDocNumberOrderByDocHistIdDesc(Long docId, Integer lswfTypeId, String docNumber);
}

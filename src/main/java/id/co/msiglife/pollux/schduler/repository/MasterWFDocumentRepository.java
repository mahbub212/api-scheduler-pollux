package id.co.msiglife.pollux.schduler.repository;

import id.co.msiglife.pollux.schduler.entity.MasterWFDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterWFDocumentRepository extends JpaRepository<MasterWFDocument, Long> {

    MasterWFDocument findByLswfTypeIdAndParentDocIdAndDocNumber(Integer lswfTypeId, Integer parentDocId, String docNumber);
    MasterWFDocument findMasterWFDocumentByDocNumberAndLswfTypeId(String docNumber, Integer lswfTypeId);

//    @Query(value = "SELECT MAX(m.MSWF_DOC_ID) FROM EKA.MST_WF_DOCUMENT m", nativeQuery = true)
//    Long findMaxParentDocId();

}

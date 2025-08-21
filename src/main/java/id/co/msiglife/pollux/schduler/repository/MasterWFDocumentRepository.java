package id.co.msiglife.pollux.schduler.repository;

import id.co.msiglife.pollux.schduler.entity.BcaData;
import id.co.msiglife.pollux.schduler.entity.MasterWFDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MasterWFDocumentRepository extends JpaRepository<MasterWFDocument, Long> {

    MasterWFDocument findByLswfTypeIdAndParentDocIdAndDocNumber(Integer lswfTypeId, Integer parentDocId, String docNumber);
    MasterWFDocument findMasterWFDocumentByDocNumberAndLswfTypeId(String docNumber, Integer lswfTypeId);

    @Query(value = "select mwd.* from eka.MST_WF_DOCUMENT mwd \n" +
            "join eka.mst_ordercl_api moa on mwd.mswf_doc_number = moa.reg_spaj \n" +
            "join eka.mst_underwriting_plx mup on mup.reg_spaj = mwd.mswf_doc_number \n" +
            "where moa.order_status = 1 and mup.final_decision in ('Postpone', 'Decline') and mwd.lswf_state_id in (-3, -2) and moa.tipe_trans = 3", nativeQuery = true)
    List<MasterWFDocument> getDataPosponeDecline();


//    @Query(value = "SELECT MAX(m.MSWF_DOC_ID) FROM EKA.MST_WF_DOCUMENT m", nativeQuery = true)
//    Long findMaxParentDocId();

}

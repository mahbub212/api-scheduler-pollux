package id.co.msiglife.pollux.schduler.repository;

import id.co.msiglife.pollux.schduler.entity.MasterUploadDokumen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface MasterUploadDokumenRepository extends JpaRepository<MasterUploadDokumen, String> {
    @Query("SELECT CASE WHEN MAX(m.dokumenId) is null then 0 else max(m.dokumenId) end FROM MasterUploadDokumen m WHERE m.regSpaj = :regSpaj")
    Integer findLastDokumenIdByRegSpaj(@Param("regSpaj") String regSpaj);

    List<MasterUploadDokumen> findByRegSpajOrderByDokumenIdDesc(String regSpaj);

    List<MasterUploadDokumen> findByRegSpajAndJenisDokumenId(String regSpaj, Integer jenisDokumenId);

    Optional<MasterUploadDokumen> findFirstByRegSpajAndJenisDokumenId(String regSpaj, Integer jenisDokumenId);

    List<MasterUploadDokumen> findByRegSpaj(String regSpaj);

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query("SELECT m FROM MasterUploadDokumen m WHERE m.regSpaj = :regSpaj")
//    @Query(value = "SELECT DOKUMEN_ID FROM EKA.MST_UPLOAD_DOKUMEN_PLX WHERE REG_SPAJ = :regSpaj ORDER BY DOKUMEN_ID DESC FETCH FIRST 1 ROW ONLY FOR UPDATE", nativeQuery = true)
    @Query(value = "SELECT DOKUMEN_ID FROM EKA.MST_UPLOAD_DOKUMEN_PLX WHERE REG_SPAJ = :regSpaj AND DOKUMEN_ID = (SELECT MAX(DOKUMEN_ID) FROM EKA.MST_UPLOAD_DOKUMEN_PLX WHERE REG_SPAJ = :regSpaj) FOR UPDATE", nativeQuery = true)
    Integer findAllByRegSpajWithLock(@Param("regSpaj") String regSpaj);

    // Find last dokumenId by regSpaj with pessimistic lock
    @Transactional
    @Query(
            value = "SELECT DOKUMEN_ID FROM EKA.MST_UPLOAD_DOKUMEN_PLX WHERE REG_SPAJ = :regSpaj AND DOKUMEN_ID = (SELECT MAX(DOKUMEN_ID) FROM EKA.MST_UPLOAD_DOKUMEN_PLX WHERE REG_SPAJ = :regSpaj) FOR UPDATE",
            nativeQuery = true
    )
    Integer findLastDokumenIdByRegSpajWithLock(@Param("regSpaj") String regSpaj);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    @Query("SELECT m FROM MasterUploadDokumen m WHERE m.regSpaj = :regSpaj ORDER BY m.dokumenId DESC")
    List<MasterUploadDokumen> findTopByRegSpajForUpdate(@Param("regSpaj") String regSpaj);


}

package id.co.msiglife.pollux.schduler.repository.polis;

import id.co.msiglife.pollux.schduler.entity.Journal.MstPtcJm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;

public interface PtcJmRepository extends JpaRepository<MstPtcJm, String> {


    @Modifying
    @Transactional
    @Query(value = "INSERT INTO eka.mst_ptc_jm " +
            "(NO_JM, NO_JURNAL, KETERANGAN, MJM_JUMLAH, MJM_CD, MJM_PROJECT, MJM_BUDGET, TYPE_TRX, KEY_JURNAL, LKU_ID, CONV_RATE, MJM_JUMLAH_USD, NO_POLIS, TAXNO) " +
            "VALUES (:noJm, :noJurnal, :keterangan, :jumlah, :cd, :project, :budget, :typeTrx, :keyJurnal, :kurs, :kursRate, :jumlahUsd, :noPolis, :taxNo)",
            nativeQuery = true)
    void insertMstPtcJm(
            @Param("noJm") String noJm,
            @Param("noJurnal") Integer noJurnal,
            @Param("keterangan") String keterangan,
            @Param("jumlah") BigDecimal jumlah,
            @Param("cd") String cd,
            @Param("project") String project,
            @Param("budget") String budget,
            @Param("typeTrx") Integer typeTrx,
            @Param("keyJurnal") String keyJurnal,
            @Param("kurs") String kurs,
            @Param("kursRate") BigDecimal kursRate,
            @Param("jumlahUsd") BigDecimal jumlahUsd,
            @Param("noPolis") String noPolis,
            @Param("taxNo") Integer taxNo
    );

    @Query(value =
            "SELECT EKA.PACGL.GET_NOJM() FROM DUAL ",
            nativeQuery = true)
    String getNoJm();

    @Query(value = "SELECT TRUNC(SYSDATE) FROM DUAL", nativeQuery = true)
    Date getTruncatedSysdate();

    @Query(value = "SELECT EKA.LST_MONTHLY_KURS.LMK_NILAI  " +
            "  FROM EKA.LST_MONTHLY_KURS  " +
            " WHERE ( EKA.LST_MONTHLY_KURS.LMK_YEAR  = :ls_year )  AND" +
            "       ( EKA.LST_MONTHLY_KURS.LMK_MONTH = :ls_month ) AND" +
            "       ( EKA.LST_MONTHLY_KURS.LKU_ID = '02' ) ", nativeQuery = true)
    BigDecimal getKursLkuId02(@Param("ls_year") String year,
                 @Param("ls_month") String month);

}


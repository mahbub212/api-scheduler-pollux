package id.co.msiglife.pollux.schduler.entity.Journal;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MST_PTC_JM", schema = "EKA")
@Data
public class MstPtcJm {

    @Id
    @Column(name = "NO_JM")
    private String noJm;

    @Column(name = "NO_JURNAL")
    private Integer noJurnal;

    @Column(name = "KETERANGAN")
    private String keterangan;

    @Column(name = "MJM_JUMLAH")
    private BigDecimal jumlah;

    @Column(name = "MJM_CD")
    private String cd;

    @Column(name = "MJM_PROJECT")
    private String project;

    @Column(name = "MJM_BUDGET")
    private String budget;

    @Column(name = "TYPE_TRX")
    private Integer typeTrx;

    @Column(name = "KEY_JURNAL")
    private String keyJurnal;

    @Column(name = "LKU_ID")
    private String kurs;

    @Column(name = "CONV_RATE")
    private BigDecimal kursRate;

    @Column(name = "MJM_JUMLAH_USD")
    private BigDecimal jumlahUsd;

    @Column(name = "NO_POLIS")
    private String noPolis;

    @Column(name = "TAXNO")
    private Integer taxNo;
}

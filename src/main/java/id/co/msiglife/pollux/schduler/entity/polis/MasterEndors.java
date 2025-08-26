package id.co.msiglife.pollux.schduler.entity.polis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MST_ENDORS", schema = "EKA")
public class MasterEndors {

    @Id
    @Column(name = "MSEN_ENDORS_NO", length = 12)
    private String msenEndorsNo;

    @Column(name = "REG_SPAJ", length = 11)
    private String regSpaj;

    @Column(name = "MSEN_INTERNAL", precision = 22, scale = 0)
    private Integer msenInternal;

    @Column(name = "MSEN_TGL_FILING")
    private Date msenTglFiling;

    @Column(name = "MSEN_TGL_KIRIM_SURAT")
    private Date msenTglKirimSurat;

    @Column(name = "MSEN_TGL_AKSEP")
    private Date msenTglAksep;

    @Column(name = "MSEN_TGL_RK")
    private Date msenTglRk;

    @Column(name = "MSEN_INPUT_DATE")
    private Date msenInputDate;

    @Column(name = "MSEN_ACTIVE_DATE")
    private Date msenActiveDate;

    @Column(name = "MSEN_TGL_TRANS")
    private Date msenTglTrans;

    @Column(name = "MSEN_RDS", precision = 22, scale = 0)
    private Integer msenRds;

    @Column(name = "MSEN_APP_SOURCE", precision = 22, scale = 0)
    private Integer msenAppSource;

    @Column(name = "FLAG_MIGRASI", precision = 22, scale = 0)
    private Integer flagMigrasi;

    @Column(name = "MSEN_USER_AKSEP", precision = 22, scale = 0)
    private Integer msenUserAksep;

    @Column(name = "MSEN_AUTO_RIDER", precision = 22, scale = 0)
    private Integer msenAutoRider;

    @Column(name = "FLAG_PS", precision = 22, scale = 0)
    private Integer flagPs;

    @Column(name = "MSEN_FLAG_UW", precision = 22, scale = 0)
    private Integer msenFlagUw;

    @Column(name = "MSEN_FLAG_TTP", precision = 22, scale = 0)
    private Integer msenFlagTtp;

    @Column(name = "MSEN_AKSEP_UW", precision = 22, scale = 0)
    private Integer msenAksepUw;

    @Column(name = "MSEN_REPROSES", precision = 22, scale = 0)
    private Integer msenReproses;

    @Column(name = "MSEN_ISSUE_SPAJ", precision = 22, scale = 0)
    private Integer msenIssueSpaj;

    @Column(name = "MSEN_ENDORS_COST", precision = 22, scale = 0)
    private BigDecimal msenEndorsCost;

    @Column(name = "MSEN_PRINT", precision = 22, scale = 0)
    private Integer msenPrint;

    @Column(name = "LSPD_ID", precision = 22, scale = 0)
    private Integer lspdId;

    @Column(name = "MSEN_TAHUN_KE", precision = 22, scale = 0)
    private Integer msenTahunKe;

    @Column(name = "MSEN_PREMI_KE", precision = 22, scale = 0)
    private Integer msenPremiKe;

    @Column(name = "MSEN_PROD_KE", precision = 22, scale = 0)
    private Integer msenProdKe;

    @Column(name = "LUS_ID", precision = 22, scale = 0)
    private Integer lusId;

    @Column(name = "MSEN_KE", precision = 22, scale = 0)
    private Integer msenKe;

    @Column(name = "MSEN_PROSES_BSM", precision = 22, scale = 0)
    private Integer msenProsesBsm;

    @Column(name = "FLAG_VEGA", length = 1)
    private String flagVega;

    @Column(name = "VEGA_REFF_ID", length = 100)
    private String vegaReffId;

    @Column(name = "MSEN_DECLINE", length = 200)
    private String msenDecline;

    @Column(name = "MSEN_NO_SSK", length = 25)
    private String msenNoSsk;

    @Column(name = "MSEN_ALASAN", length = 500)
    private String msenAlasan;

    @Lob
    @Column(name = "MSEN_SURAT", columnDefinition = "LONG")
    private String msenSurat;

    @Column(name = "MSEN_SEQ", precision = 22, scale = 0)
    private Long msenSeq;

}

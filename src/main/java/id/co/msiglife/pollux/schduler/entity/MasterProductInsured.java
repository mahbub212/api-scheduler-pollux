package id.co.msiglife.pollux.schduler.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import id.co.msiglife.pollux.schduler.composite.CompMasterProductInsured;
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
@IdClass(CompMasterProductInsured.class)
@Table(name = "MST_PRODUCT_INSURED", schema = "EKA")
public class MasterProductInsured {

    @Id
    @Column(name = "REG_SPAJ")
    private String regSpaj;

    @Id
    @Column(name = "MSTE_INSURED_NO")
    private Integer msteInsuredNo;

    @Id
    @Column(name = "LSBS_ID")
    private Integer lsbsId;

    @Id
    @Column(name = "LSDBS_NUMBER")
    private Integer lsdbsNumber;

    @Column(name = "LKU_ID")
    private String lkuId;

    @Column(name = "MSPR_BEG_DATE")
    @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
    private Date begDate;

    @Column(name = "MSPR_END_DATE")
    @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
    private Date endDate;

    @Column(name = "MSPR_TSI")
    private BigDecimal tsi;

    @Column(name = "MSPR_TSI_PA_A")
    private BigDecimal tsiPaA;

    @Column(name = "MSPR_TSI_PA_B")
    private BigDecimal tsiPaB;

    @Column(name = "MSPR_TSI_PA_C")
    private BigDecimal tsiPaC;

    @Column(name = "MSPR_TSI_PA_D")
    private BigDecimal tsiPaD;

    @Column(name = "MSPR_TSI_PA_M")
    private BigDecimal tsiPaM;

    @Column(name = "MSPR_CLASS")
    private Integer msprClass;

    @Column(name = "MSPR_UNIT")
    private Integer unit;

    @Column(name = "MSPR_RATE")
    private Double rate;

    @Column(name = "MSPR_PERSEN")
    private Integer persen;

    @Column(name = "MSPR_PREMIUM")
    private Double premium;

    @Column(name = "MSPR_DISCOUNT")
    private Double discount;

    @Column(name = "MSPR_REF_NO")
    private String refNo;

    @Column(name = "MSPR_ACTIVE")
    private Integer active;

    @Column(name = "MSPR_EXTRA")
    private Double extra;

    @Column(name = "MSPR_INS_PERIOD")
    private Integer insPeriod;

    @Column(name = "MSPR_BUNGA_KPR")
    private Double bungaKpr;

    @Column(name = "MSPR_INS_BULAN")
    private Integer insBulan;

    @Column(name = "MSPR_SISA_LIMIT")
    private BigDecimal sisaLimit;

    @Column(name = "MSPR_WAIT_PERIODE")
    private Integer waitPeriode;

    @Column(name = "MSPR_UJROH")
    private BigDecimal ujroh;

    @Column(name = "MSPR_TABARRU")
    private BigDecimal tabarru;

    @Column(name = "MSPR_END_PAY")
    @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
    private Date endPay;

    @Column(name = "MSPR_TT")
    private Integer tt;

    @Column(name = "MSPR_PINJM_AWAL")
    private BigDecimal pinjmAwal;

    @Column(name = "MSPR_LPINJAM")
    private Integer lpinjam;

    @Column(name = "MSPR_PU_PTB")
    private Integer puPtb;

    @Column(name = "MSPR_NONACTIVE_DATE")
    @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
    private Date nonactiveDate;

    @Column(name = "FLAG_UP_SC")
    private Integer flagUpSc;

    @Column(name = "APE_PROD")
    private Double apeProd;

    @Column(name = "MSPR_TSI_ENDORS")
    private BigDecimal tsiEndors;

    @Column(name = "MSPR_FLAG_JENIS")
    private Integer flagJenis;

    @Column(name = "MSPR_INS_PERIOD_PARAM")
    private Integer insPeriodParam;

    @Column(name = "MSPR_URUT")
    private Integer urut;

    @Column(name = "LDP_PLAN")
    private Integer ldpPlan;

    @Column(name = "MSPR_WAIVE_PERIOD")
    private Integer waivePeriod;

    @Column(name = "MSPR_WAITING_DATE")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date waitingDate;

    @Column(name = "FLAG_DEDUCTIBLE")
    private Integer flagDeductible;
}

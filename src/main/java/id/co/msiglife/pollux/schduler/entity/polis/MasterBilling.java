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
@Table(name = "MST_BILLING", schema = "EKA")
public class MasterBilling {

    // NUMBER(22,0) -> Integer (flag)
    @Column(name = "MSBI_RENEWAL")
    private Integer msbiRenewal;

    // NUMBER(22,0)
    @Column(name = "MSBI_FLAG_KUITANSI")
    private Integer msbiFlagKuitansi;

    // CHAR(9)
    @Column(name = "MSBI_NO_JM")
    private String msbiNoJm;

    // CHAR(9)
    @Column(name = "MSBI_NO_JM_BTL")
    private String msbiNoJmBtl;

    // NUMBER(22,0) -> nilai, pakai BigDecimal aman
    @Column(name = "MSBI_NB")
    private BigDecimal msbiNb;

    // DATE
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "MSBI_TGL_DEBET")
    private Date msbiTglDebet;

    // NUMBER(22,0) -> BigDecimal (nominal)
    @Column(name = "MSBI_PAID")
    private BigDecimal msbiPaid;

    // NUMBER(22,0)
    @Column(name = "MSBI_ACTIVE")
    private Integer msbiActive;

    // NUMBER(22,0)
    @Column(name = "MSBI_PRINT")
    private Integer msbiPrint;

    // NUMBER(22,0)
    @Column(name = "MSBI_ADD_BILL")
    private Integer msbiAddBill;

    // NUMBER(22,0) -> Integer (bukan String)
    @Column(name = "MSBI_FLAG_WS")
    private Integer msbiFlagWs;

    // NUMBER(22,0)
    @Column(name = "TRANCHE_ID")
    private Long trancheId;

    // VARCHAR2(35) -> String (bukan Long)
    @Column(name = "BILL_PROJECTION_ID")
    private String billProjectionId;

    // CHAR(11)
    @Id
    @Column(name = "REG_SPAJ")
    private String regSpaj;

    // NUMBER(22,0)
    @Column(name = "MSBI_TAHUN_KE")
    private Integer msbiTahunKe;

    // NUMBER(22,0)
    @Column(name = "MSBI_PREMI_KE")
    private Integer msbiPremiKe;

    // VARCHAR2(50)
    @Column(name = "MSBI_BILL_NO")
    private String msbiBillNo;

    // DATE
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "MSBI_BEG_DATE")
    private Date msbiBegDate;

    // DATE
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "MSBI_END_DATE")
    private Date msbiEndDate;

    // DATE
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "MSBI_DUE_DATE")
    private Date msbiDueDate;

    // DATE
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "MSBI_AKTIF_DATE")
    private Date msbiAktifDate;

    // CHAR(2)
    @Column(name = "LKU_ID")
    private String lkuId;

    // NUMBER(22,0) -> biaya: BigDecimal
    @Column(name = "MSBI_POLICY_COST")
    private BigDecimal msbiPolicyCost;

    @Column(name = "MSBI_HCR_POLICY_COST")
    private BigDecimal msbiHcrPolicyCost;

    @Column(name = "MSBI_TTL_CARD_COST")
    private BigDecimal msbiTtlCardCost;

    @Column(name = "MSBI_STAMP")
    private BigDecimal msbiStamp;

    // DATE
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "MSBI_INPUT_DATE")
    private Date msbiInputDate;

    // NUMBER(22,0)
    @Column(name = "MSBI_REMAIN")
    private BigDecimal msbiRemain;

    // NUMBER(22,0)
    @Column(name = "MSBI_FLAG_SISA")
    private Integer msbiFlagSisa;

    // NUMBER(22,0)
    @Column(name = "LUS_ID")
    private Long lusId;

    // NUMBER(22,0)
    @Column(name = "LSPD_ID")
    private Long lspdId;

    // DATE
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "MSBI_PAID_DATE")
    private Date msbiPaidDate;

    // DATE
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "MSBI_TGL_BATAL")
    private Date msbiTglBatal;

    // CHAR(11)
    @Column(name = "MSBI_HCR_NO")
    private String msbiHcrNo;

    // CHAR(2)
    @Column(name = "LCA_ID")
    private String lcaId;

    // CHAR(2)
    @Column(name = "LWK_ID")
    private String lwkId;

    // CHAR(2)
    @Column(name = "LSRG_ID")
    private String lsrgId;

    // DATE
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "MSBI_PRINT_DATE")
    private Date msbiPrintDate;

    // NUMBER(22,0)
    @Column(name = "MSBI_GROSS")
    private BigDecimal msbiGross;

    // NUMBER(22,0)
    @Column(name = "MSBI_FLAG_RECUR")
    private Integer msbiFlagRecur;

    // NUMBER(22,0)
    @Column(name = "MSBI_SMS")
    private Integer msbiSms;

    // NUMBER(22,0)
    @Column(name = "MSBI_FLAG_TOPUP")
    private Integer msbiFlagTopup;

    // NUMBER(22,0)
    @Column(name = "MSBI_PERSEN_PAID")
    private BigDecimal msbiPersenPaid;

    // NUMBER(22,0)
    @Column(name = "MSBI_TH_BAK")
    private Integer msbiThBak;

    // NUMBER(22,0)
    @Column(name = "MSBI_PREMI_BAK")
    private Integer msbiPremiBak;

    // VARCHAR2(35) -> String
    @Column(name = "MSBA_ID")
    private String msbaId;

    // CHAR(1) -> String/Character;
    @Column(name = "FLAG_SYNC")
    private String flagSync;

    // VARCHAR2(50) -> String (bukan BigDecimal)
    @Column(name = "PAYMENT_OTHER")
    private String paymentOther;

    // NUMBER(22,0) -> BigDecimal (nilai)
    @Column(name = "MSBI_BUNGA_TUNGGAKAN")
    private BigDecimal msbiBungaTunggakan;

    // NUMBER(22,0)
    @Column(name = "MSBI_FLAG_VA")
    private Integer msbiFlagVa;

    // NUMBER(22,0) -> Long/Integer
    @Column(name = "SAVE_SERIES")
    private Long saveSeries;
}
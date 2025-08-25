package id.co.msiglife.pollux.schduler.entity.CancelPolis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MST_BILLING", schema = "EKA")
public class MasterBilling {

    @Column(name = "MSBI_RENEWAL")
    private String msbiRenewal;

    @Column(name = "MSBI_FLAG_KUITANSI")
    private String msbiFlagKuitansi;

    @Column(name = "MSBI_NO_JM")
    private String msbiNoJm;

    @Column(name = "MSBI_NO_JM_BTL")
    private String msbiNoJmBtl;

    @Column(name = "MSBI_NB")
    private BigDecimal msbiNb;

    @Column(name = "MSBI_TGL_DEBET")
    private LocalDate msbiTglDebet;

    @Column(name = "MSBI_PAID")
    private BigDecimal msbiPaid;

    @Column(name = "MSBI_ACTIVE")
    private Integer msbiActive;

    @Column(name = "MSBI_PRINT")
    private Integer msbiPrint;

    @Column(name = "MSBI_ADD_BILL")
    private Integer msbiAddBill;

    @Column(name = "MSBI_FLAG_WS")
    private String msbiFlagWs;

    @Column(name = "TRANCHE_ID")
    private Long trancheId;

    @Column(name = "BILL_PROJECTION_ID")
    private Long billProjectionId;

    @Id
    @Column(name = "REG_SPAJ")
    private String regSpaj;

    @Column(name = "MSBI_TAHUN_KE")
    private Integer msbiTahunKe;

    @Column(name = "MSBI_PREMI_KE")
    private Integer msbiPremiKe;

    @Column(name = "MSBI_BILL_NO")
    private String msbiBillNo;

    @Column(name = "MSBI_BEG_DATE")
    private Date msbiBegDate;

    @Column(name = "MSBI_END_DATE")
    private Date msbiEndDate;

    @Column(name = "MSBI_DUE_DATE")
    private Date msbiDueDate;

    @Column(name = "MSBI_AKTIF_DATE")
    private Date msbiAktifDate;

    @Column(name = "LKU_ID")
    private String lkuId;

    @Column(name = "MSBI_POLICY_COST")
    private BigDecimal msbiPolicyCost;

    @Column(name = "MSBI_HCR_POLICY_COST")
    private BigDecimal msbiHcrPolicyCost;

    @Column(name = "MSBI_TTL_CARD_COST")
    private BigDecimal msbiTtlCardCost;

    @Column(name = "MSBI_STAMP")
    private BigDecimal msbiStamp;

    @Column(name = "MSBI_INPUT_DATE")
    private Date msbiInputDate;

    @Column(name = "MSBI_REMAIN")
    private BigDecimal msbiRemain;

    @Column(name = "MSBI_FLAG_SISA")
    private Integer msbiFlagSisa;

    @Column(name = "LUS_ID")
    private Long lusId;

    @Column(name = "LSPD_ID")
    private Long lspdId;

    @Column(name = "MSBI_PAID_DATE")
    private Date msbiPaidDate;

    @Column(name = "MSBI_TGL_BATAL")
    private Date msbiTglBatal;

    @Column(name = "MSBI_HCR_NO")
    private String msbiHcrNo;

    @Column(name = "LCA_ID")
    private String lcaId;

    @Column(name = "LWK_ID")
    private String lwkId;

    @Column(name = "LSRG_ID")
    private String lsrgId;

    @Column(name = "MSBI_PRINT_DATE")
    private Date msbiPrintDate;

    @Column(name = "MSBI_GROSS")
    private BigDecimal msbiGross;

    @Column(name = "MSBI_FLAG_RECUR")
    private Integer msbiFlagRecur;

    @Column(name = "MSBI_SMS")
    private Integer msbiSms;

    @Column(name = "MSBI_FLAG_TOPUP")
    private Integer msbiFlagTopup;

    @Column(name = "MSBI_PERSEN_PAID")
    private BigDecimal msbiPersenPaid;

    @Column(name = "MSBI_TH_BAK")
    private Integer msbiThBak;

    @Column(name = "MSBI_PREMI_BAK")
    private Integer msbiPremiBak;

    @Column(name = "MSBA_ID")
    private Long msbaId;

    @Column(name = "FLAG_SYNC")
    private Integer flagSync;

    @Column(name = "PAYMENT_OTHER")
    private BigDecimal paymentOther;

    @Column(name = "MSBI_BUNGA_TUNGGAKAN")
    private BigDecimal msbiBungaTunggakan;

    @Column(name = "MSBI_FLAG_VA")
    private Integer msbiFlagVa;

    @Column(name = "SAVE_SERIES")
    private String saveSeries;

}

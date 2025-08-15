package id.co.msiglife.pollux.schduler.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MST_POLICY", schema = "EKA")
public class MasterPolicy {

	@Id
	@Column(name = "REG_SPAJ")
	private String regSpaj;

	@Column(name = "LSTB_ID")
	private Integer lstbId;

	@Column(name = "LCA_ID")
	private String lcaId;

	@Column(name = "LWK_ID")
	private String lwkId;

	@Column(name = "LSRG_ID")
	private String lsrgId;

	@Column(name = "MPI_MOU")
	private String noMou;

	@Column(name = "LSSP_ID")
	private Integer lsspId;

	@Column(name = "LSPD_ID")
	private Integer lspdId;

	@Column(name = "MSPO_POLICY_HOLDER")
	private String policyHolder;

	@Column(name = "LSRE_ID")
	private Integer lsreId;

	@Column(name = "MSPO_AGE")
	private Integer age;

	@Column(name = "LKU_ID")
	private String lkuId;

	@Column(name = "LSCB_ID")
	private Integer lscbId;

	@Column(name = "LUS_ID")
	private Integer lusId;

	@Column(name = "MSPO_POLICY_NO")
	private String policyNo;

	@Column(name = "MSPO_SPAJ_DATE")
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
	private Date spajDate;

	@Column(name = "MSPO_INS_PERIOD")
	private Integer insPeriod;

	@Column(name = "MSPO_PAY_PERIOD")
	private Integer payPeriod;

	@Column(name = "MSPO_PROSES_BILL")
	private Integer prosesBill;

	@Column(name = "MSPO_NEXT_BILL")
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
	private Date nextBill;

	@Column(name = "MSPO_INSTALLMENT")
	private Integer installment;

	@Column(name = "MSPO_FLAT")
	private Integer flat;

	@Column(name = "MSPO_REF_BII")
	private Integer refBii;

	@Column(name = "MSPO_JN_COAS")
	private Integer jnCoas;

	@Column(name = "MSPO_DATE_PRINT")
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
	private Date datePrint;

	@Column(name = "MSPO_DATE_SPEC")
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
	private Date dateSpec;

	@Column(name = "MSPO_DATE_TTP")
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
	private Date dateTtp;

	@Column(name = "MSPO_PRIBADI")
	private Integer pribadi;

	@Column(name = "MSPO_AO")
	private String ao;

	@Column(name = "MSPO_CUSTOMER")
	private String customer;

	@Column(name = "MSPO_NO_BLANKO")
	private String noBlanko;

	@Column(name = "MSPO_NO_POL_ASM")
	private String noPolAsm;

	@Column(name = "MSPO_UNDER_TABLE")
	private Double underTable;

	@Column(name = "MSPO_INS_BULAN")
	private Integer insBulan;

	@Column(name = "MSPO_BEASISWA")
	private String beasiswa;

	@Column(name = "MSPO_LAMA_BEASISWA")
	private Integer lamaBeasiswa;

	@Column(name = "MSPO_CARA_BEASISWA")
	private Integer caraBeasiswa;

	@Column(name = "MSPO_UMUR_BEASISWA")
	private Integer umurBeasiswa;

	@Column(name = "MSPO_OLDPOL_EB")
	private String oldpolEb;

	@Column(name = "MSPO_FOLLOW_UP")
	private Integer followUp;

	@Column(name = "MSPO_KOMISI_BII")
	private Integer komisiBii;

	@Column(name = "MSPO_INPUT_DATE")
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
	private Date inputDate;

	@Column(name = "MSPO_BEG_DATE")
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
	private Date begDate;

	@Column(name = "MSPO_END_DATE")
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
	private Date endDate;

	@Column(name = "MSPO_PROVIDER")
	private Integer provider;

	@Column(name = "MSPO_PREEXISTING")
	private Integer preexisting;

	@Column(name = "MSPO_EXTEND")
	private Double extend;

	@Column(name = "MSPO_PASS_EB")
	private String passEb;

	@Column(name = "MSPO_BIAYA_TRANSFER")
	private Double biayaTransfer;

	@Column(name = "MSPO_AS_CHARGES")
	private Integer asCharges;

	@Column(name = "MSPO_PLAN_PROVIDER")
	private String planProvider;

	@Column(name = "MSPO_ASO")
	private Integer aso;

	@Column(name = "LSTP_ID")
	private Integer lstpId;

	@Column(name = "MSPO_NOTE")
	private String note;

	@Column(name = "MSPO_NO_KERJASAMA")
	private String noKerjasama;

	@Column(name = "MSPO_NASABAH_ACC")
	private String nasabahAcc;

	@Column(name = "MSPO_NASABAH_DCIF")
	private String nasabahDcif;

	@Column(name = "MSPO_POLICY_NO_FORMAT")
	private String policyNoFormat;

	@Column(name = "MSPO_SYAHRIAH")
	private Integer syahriah;

	@Column(name = "MSPO_CASH_BACK")
	private Integer cashBack;

	@Column(name = "MSPO_CASH_MKT")
	private String cashMkt;

	@Column(name = "MSPO_CASH_ENTRY")
	private String cashEntry;

	@Column(name = "MSPO_CASH_VERIFIKASI")
	private String cashVerifikasi;

	@Column(name = "MSPO_CASH_DPP")
	private String cashDpp;

	@Column(name = "MSPO_CASH_WIL")
	private Integer cashWil;

	@Column(name = "MSPO_CASH_RATE_1")
	private Double cashRate1;

	@Column(name = "MSPO_CASH_RATE_2")
	private Double cashRate2;

	@Column(name = "MSPO_JENIS_TERBIT")
	private Integer jenisTerbit;

	@Column(name = "MSPO_SELF_INS")
	private Integer selfIns;

	@Column(name = "MSPO_JOINT_LIFE")
	private Integer jointLife;

	@Column(name = "MSPO_JOINT_POLICY")
	private String jointPolicy;

	@Column(name = "MSPO_WILAYAH_MRI")
	private Integer wilayahMri;

	@Column(name = "MSPO_NO_INPLAN")
	private String noInplan;

	@Column(name = "MSPO_LEADER")
	private String leader;

	@Column(name = "MSPO_SPV_DMTM")
	private String spvDmtm;

	@Column(name = "MSPO_TERIMA_TTP")
	@JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
	private Date terimaTtp;

	@Column(name = "MSPO_BUNDLE")
	private Integer bundle;

	@Column(name = "MSPO_FLAG_SPH")
	private Integer flagSph;

	@Column(name = "MSPO_CALL_DATE")
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
	private Date callDate;

	@Column(name = "MSPO_JNS_CETAK")
	private Integer jnsCetak;

	@Column(name = "MSPO_FLAG_NEW")
	private Integer flagNew;

	@Column(name = "MSPO_FLAG_LOST_PREMI")
	private Integer flagLostPremi;

	@Column(name = "MSPO_NAMA_KOMPETITOR")
	private String namaKompetitor;

	@Column(name = "MSPO_INSTALLMENT_YEAR")
	private Integer installmentYear;

	@Column(name = "MSPO_INSTALLMENT_YEAR2")
	private Integer installmentYear2;

	@Column(name = "MSPO_SPAJ_BUNDLE")
	private String spajBundle;

	@Column(name = "NO_PROPOSAL")
	private String noProposal;

	@Column(name = "APPLICATION_ID_DMTM")
	private String applicationIdDmtm;

	@Column(name = "MSAG_ASNEW")
	private Integer msagAsNew;

	@Column(name = "FLAG_EMAIL")
	private Integer flagEmail;

	@Column(name = "REFF_TM_ID")
	private String reffTmId;

	@Column(name = "MSPO_NO_PENGIRIMAN")
	private String noPengiriman;

	@Column(name = "MSPO_FLAG_SPAJ")
	private Integer flagSpaj;

	@Column(name = "MSPO_KORESPONDENSI")
	private Integer korespondensi;

	@Column(name = "MSPO_PAYER")
	private String payer;

	@Column(name = "MSPO_JENIS_PAYER")
	private Integer jenisPayer;

	@Column(name = "LSRE_ID_PAYER")
	private Integer lsreIdPayer;

	@Column(name = "LEAD_ID")
	private String leadId;

	@Column(name = "MSPO_UPDATING_DATE")
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
	private Date updatingDate;

	@Column(name = "MSPO_MEDICAL")
	private Integer medical;

	@Column(name = "MSPO_INPUTAN_BANK_MRI")
	private Integer inputanBankMri;

	@Column(name = "CF_JOB_CODE")
	private String cfJobCode;

	@Column(name = "CF_CUSTOMER_ID")
	private String cfCustomerId;

	@Column(name = "CF_CAMPAIGN_CODE")
	private String cfCampaignCode;

	@Column(name = "MSPO_ID_SPONSOR")
	private String idSponsor;

	@Column(name = "MSPO_ID_PLACE")
	private String idPlace;

	@Column(name = "MSPO_CRP_SCORE")
	private Integer crpScore;

	@Column(name = "MSPO_CRP_RESULT")
	private String crpResult;

	@Column(name = "PEGA_CASE_ID")
	private String pegaCaseId;

	@Column(name = "NO_BATCH")
	private String noBatch;

	@Column(name = "FLAG_BATCH")
	private String flagBatch;

	@Column(name = "FLAG_KPR")
	private Integer flagKpr;

	@Column(name = "FLAG_KUD")
	private Integer flagKud;

	@Column(name = "MCP_CAMPAIGN_ID")
	private String mcpCampaignId;

	@Column(name = "COVID_FLAG")
	private String covidFlag;

	@Column(name = "FLAG_VIP")
	private String flagVip;

	@Column(name = "FULL_AUTOSALES")
	private String fullAutosales;

	@Column(name = "MSPO_FLAG_PAPER")
	private Integer flagPaper;

	@Column(name = "FLAG_PRODUCER_CLUB")
	private Integer producerClub;

	@Column(name = "FLAG_MDRT")
	private Integer mdrt;

	@Column(name = "MCS_ID")
	private String mcsId;

	@Column(name = "MOS_ID")
	private String mosId;

	@Column(name = "MMS_ID")
	private String mmsId;

	@Column(name = "FLAG_DEDUCT")
	private Integer flagDeduct;

	@Column(name = "RETIRE_AGE")
	private Integer retireAge;

	@Column(name = "FLAG_NLG")
	private Integer flagNlg;

	@Column(name = "FLAG_PAYMENT_TYPE")
	private Integer flagPaymentType;

	@Column(name = "PKS_ID")
	private String pksId;

	@Column(name = "PRODUCT_ID")
	private String productId;

	@Column(name = "FLAG_BACKUP")
	private String flagBackup;

}
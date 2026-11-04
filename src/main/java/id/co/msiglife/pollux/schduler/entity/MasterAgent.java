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
@Table(name = "MST_AGENT", schema = "EKA")
public class MasterAgent {

	@Id
	@Column(name = "MSAG_ID")
	private String msagId;

	@Column(name = "LCA_ID")
	private String lcaId;

	@Column(name = "LWK_ID")
	private String lwkId;

	@Column(name = "LSRG_ID")
	private String lsrgId;

	@Column(name = "MCL_ID")
	private String mclId;

	@Column(name = "MST_LEADER")
	private String mstLeader;

	@Column(name = "LSTB_ID")
	private Integer lstbId;

	@Column(name = "LSLE_ID")
	private Integer lsleId;

	@Column(name = "MSAG_JENIS")
	private Integer jenis;

	@Column(name = "MSAG_BEG_DATE")
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
	private Date begDate;

	@Column(name = "MSAG_END_DATE")
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
	private Date endDate;

	@Column(name = "MSAG_ACTIVE_DATE")
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
	private Date activeDate;

	@Column(name = "MSAG_INPUT_DATE")
	@JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
	private Date inputDate;

	@Column(name = "MSAG_PROD_1")
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
	private Date prod1;

	@Column(name = "MSAG_ACTIVE")
	private Integer active;

	@Column(name = "MSAG_TAX")
	private Double tax;

	@Column(name = "LUS_ID")
	private Integer lusId;

	@Column(name = "MSAG_KOMISI")
	private Integer komisi;

	@Column(name = "MSAG_TABUNGAN")
	private String tabungan;

	@Column(name = "LBN_ID")
	private Integer lbnId;

	@Column(name = "REKRUTER")
	private String rekruter;

	@Column(name = "MSAG_FLAG_BM")
	private Integer flagBm;

	@Column(name = "MSAG_SBM")
	private Integer sbm;

	@Column(name = "MSAG_COA")
	private Integer coa;

	@Column(name = "MSAG_ULINK")
	private Integer ulink;

	@Column(name = "MSAG_RM")
	private String rm;

	@Column(name = "MSAG_SERTIFIKAT")
	private Integer sertifikat;

	@Column(name = "MSAG_BERLAKU")
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
	private Date berlaku;

	@Column(name = "MSAG_SERTIFIKAT_NO")
	private String sertifikatNo;

	@Column(name = "REGBANK")
	private String regBank;

	@Column(name = "MSAG_AAKM")
	private Integer aakm;

	@Column(name = "MSAG_FAD")
	private Integer fad;

	@Column(name = "JPERSON")
	private Integer jperson;

	@Column(name = "MSAG_BAY")
	private Integer bay;

	@Column(name = "MSAG_SP")
	private Integer sp;

	@Column(name = "MSAG_AAKM_LEAD")
	private String aakmLead;

	@Column(name = "MSAG_LENGKAP")
	private Integer lengkap;

	@Column(name = "MSAG_SMART_FLAG")
	private Integer smartFlag;

	@Column(name = "MSAG_SMART_NO")
	private String smartNo;

	@Column(name = "MSAG_EB")
	private String eb;

	@Column(name = "MSAG_PIN_FLAG")
	private Integer pinFlag;

	@Column(name = "MSAG_BOP_FLAG")
	private Integer bopFlag;

	@Column(name = "MSAG_BOP_DATE")
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
	private Date bopDate;

	@Column(name = "MSAG_SIMCARD_FLAG")
	private Integer simcardFlag;

	@Column(name = "MSAG_ATM")
	private String atm;

	@Column(name = "MSAG_NPWP")
	private String npwp;

	@Column(name = "MSAG_FLAG")
	private Integer flag;

	@Column(name = "LSPT_ID")
	private Integer lsptId;

	@Column(name = "MSAG_KRY")
	private Integer kry;

	@Column(name = "MSAG_NIK")
	private String nik;

	@Column(name = "MSAG_CS")
	private Integer cs;

	@Column(name = "MSAG_TUTOR_ID")
	private Integer tutorId;

	@Column(name = "MSAG_TUTOR_DATE")
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
	private Date tutorDate;

	@Column(name = "LAC_ID")
	private String lacId;

	@Column(name = "MSAG_ACD")
	private Integer acd;

	@Column(name = "FINGERID")
	private String fingerid;

	@Column(name = "MSAG_TE")
	private Integer te;

	@Column(name = "MSAG_BSB")
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
	private Date bsb;

	@Column(name = "LVL_FCD")
	private Integer lvlFcd;

	@Column(name = "LAN_ID")
	private String lanId;

	@Column(name = "FLAG_BP_AP")
	private Integer flagBpAp;

	@Column(name = "CLA_ID")
	private Integer claId;

	@Column(name = "LAC_LEADER")
	private String lacLeader;

	@Column(name = "OS_LEVEL")
	private Integer osLevel;

	@Column(name = "LADC_ID")
	private String ladcId;

	@Column(name = "MSAG_GWS")
	private Integer gws;

	@Column(name = "MSAG_MWS")
	private Integer mws;

	@Column(name = "MSAG_FOUNDER")
	private Integer founder;

	@Column(name = "MSAG_TSR")
	private Integer tsr;

	@Column(name = "LSB_ID")
	private String lsbId;

	@Column(name = "MSAG_ASNEW")
	private Integer asnew;

	@Column(name = "MSAG_SIMCARD_NO")
	private String simcardNo;

	@Column(name = "MST_LEADER_COMM")
	private String mstLeaderComm;

	@Column(name = "FLAG_TRANS")
	private Integer flagTrans;

	@Column(name = "TGL_AWAl_LISENSI")
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
	private Date tglAwalLisensi;

	@Column(name = "TM_ID")
	private String tmId;

	@Column(name = "MSAG_SERTIFIKAT_FLAG")
	private Integer sertifikatFlag;

	@Column(name = "MSAG_FLAG_ADB")
	private Integer flagAdb;

	@Column(name = "MSAG_MYTEAM")
	private Integer myteam;

	@Column(name = "MSAG_PROXY")
	private Integer proxy;

	@Column(name = "MSAG_PROXY_DEADLINE")
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
	private Date proxyDeadline;

	@Column(name = "MSAG_INORGANIC")
	private Integer inorganic;

	@Column(name = "MSAG_SERTIFIKAT_SYARIAH")
	private Integer sertifikatSyariah;

	@Column(name = "MSAG_BERLAKU_SYARIAH")
	@JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
	private Date berlakuSyariah;

	@Column(name = "MSAG_SERTIFIKAT_NO_SYARIAH")
	private String sertifikatNoSyariah;

	@Column(name = "SRV_MSAG_ID")
	private String srvMsagId;

	@Column(name = "MSAG_COMPLIANCE_DATE")
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
	private Date complianceDate;

	@Column(name = "MSAG_ID_RECRUIT")
	private String idRecruit;

	@Column(name = "LSBP_ID")
	private Integer lsbpId;

	@Column(name = "MSAG_NPWP_PI")
	private String npwpPi;

	@Column(name = "STATUS_NPWP")
	private Integer statusNpwp;

	@Column(name = "LJB_ID")
	private Integer ljbId;

	@Column(name = "DUA_DATE")
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
	private Date duaDate;

	@Column(name = "DUA_STATUS")
	private Integer duaStatus;

	@Column(name = "JOIN_DATE")
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
	private Date joinDate;

	@Column(name = "LDE_DEPT")
	private String ldeDept;

	@Column(name = "LDI_DIVISION")
	private String ldiDivision;

	@Column(name = "LJB_NOTE")
	private String ljbNote;

	@Column(name = "LWK_NAMA")
	private String lwkNama;

	@Column(name = "END_CONT_DATE")
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
	private Date endContDate;
    
}
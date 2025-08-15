package id.co.msiglife.pollux.schduler.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import id.co.msiglife.pollux.schduler.composite.CompMasterInsured;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(CompMasterInsured.class)
@Table(name = "MST_INSURED", schema = "EKA")
public class MasterInsured {

    @Id
    @Column(name = "REG_SPAJ")
    private String regSpaj;

    @Id
    @Column(name = "MSTE_INSURED_NO")
    private Integer insuredNo;

    @Column(name = "MSTE_INSURED")
    private String insured;

    @Column(name = "MSTE_AGE")
    private Integer age;

    @Column(name = "MSTE_MEDICAL")
    private Integer medical;

    @Column(name = "MSTE_STANDARD")
    private Integer standard;

    @Column(name = "LSPD_ID")
    private Integer lspdId;

    @Column(name = "LUS_ID")
    private Integer lusId;

    @Column(name = "MSTE_CARD_COST")
    private Double cardCost;

    @Column(name = "MSTE_BEG_DATE")
    @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
    private Date begDate;

    @Column(name = "MSTE_END_DATE")
    @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
    private Date endDate;

    @Column(name = "MSTE_INPUT_DATE")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date inputDate;

    @Column(name = "MSTE_BACKUP")
    private Integer backup;

    @Column(name = "LSSA_ID")
    private Integer lssaId;

    @Column(name = "MSTE_REAS")
    private Integer reas;

    @Column(name = "MSTE_ACTIVE")
    private Integer active;

    @Column(name = "MSTE_DEWASA")
    private Integer dewasa;

    @Column(name = "MSTE_FLAG_CC")
    private Integer flagCc;

    @Column(name = "MSTE_TGL_RECUR")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date tglRecur;

    @Column(name = "MSTE_FLAG_RECUR")
    private Integer flagRecur;

    @Column(name = "MSTE_NO_REG")
    private String noReg;

    @Column(name = "MSTE_TGL_BATAL")
    @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
    private Date tglBatal;

    @Column(name = "MSTE_PRE_EXITING")
    private Integer preExiting;

    @Column(name = "MSTE_TGL_AKSEP")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date tglAksep;

    @Column(name = "MSTE_TGL_SPAJ")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date tglSpaj;

    @Column(name = "NO_INVOICE")
    private String noInvoice;

    @Column(name = "MSTE_FLAG_UBAH")
    private Integer flagUbah;

    @Column(name = "MSTE_PCT_DPLK")
    private Double pctDplk;

    @Column(name = "MSTE_FLAG_EL")
    private Integer flagEl;

    @Column(name = "MSTE_FLAG_INVESTASI")
    private Integer flagInvestasi;

    @Column(name = "MSTE_TGL_TERIMA_SPAJ")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date tglTerimaSpaj;

    @Column(name = "MSTE_TGL_KIRIM_POLIS")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date tglKirimPolis;

    @Column(name = "MSTE_TGL_SPAJ_DOC")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date tglSpajDoc;

    @Column(name = "MSTE_FLAG_GUTHRIE")
    private Integer flagGuthrie;

    @Column(name = "MSTE_FLAG_KOMISI")
    private Integer flagKomisi;

    @Column(name = "MSTE_TGL_KOMISI")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date tglKomisi;

    @Column(name = "MSTE_SPAJ_ASLI")
    private Integer spajAsli;

    @Column(name = "MSTE_TGL_SETUJU")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date tglSetuju;

    @Column(name = "MSTE_KYC_RESULT")
    private String kycResult;

    @Column(name = "MSTE_KYC_LUS_ID")
    private Integer kycLusId;

    @Column(name = "MSTE_KYC_DATE")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date kycDate;

    @Column(name = "MSTE_FLAG_UPDATE_NASABAH")
    private Integer flagUpdateNasabah;

    @Column(name = "MSTE_FLAG_UW")
    private Integer flagUw;

    @Column(name = "MSTE_FLAG_UKM")
    private Integer flagUkm;

    @Column(name = "MSTE_FLAG_DIREC")
    private Integer flagDirec;

    @Column(name = "MSTE_TGL_KIRIM_LB")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date tglKirimLb;

    @Column(name = "MSTE_TGL_TERIMA_LB")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date tglTerimaLb;

    @Column(name = "MSTE_TGL_VALID_PRINT")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date tglValidPrint;

    @Column(name = "MSTE_BSM_LINK")
    private Integer bsmLink;

    @Column(name = "MSTE_NO_VACC")
    private String noVacc;

    @Column(name = "MSTE_FLAG_REF_TELEMARKET")
    private Integer flagRefTelemarket;

    @Column(name = "MSTE_TGL_ADMIN")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date tglAdmin;

    @Column(name = "KODE_GIFT")
    private String kodeGift;

    @Column(name = "MSTE_TGL_KIRIM_ADMEDIKA")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date tglKirimAdmedika;

    @Column(name = "MSTE_TGL_TERIMA_ADMEDIKA")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date tglTerimaAdmedika;

    @Column(name = "MSTE_TGL_TERIMA_ADMIN")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date tglTerimaAdmin;

    @Column(name = "FLAG_PBL")
    private Integer flagPbl;

    @Column(name = "MSTE_FLAG_UPLOAD_SCAN")
    private Integer flagUploadScan;

    @Column(name = "MSTE_TGL_TERIMA_NASABAH")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date tglTerimaNasabah;

    @Column(name = "MSTE_TGL_TERIMA_GA_FROM_UW")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date tglTerimaGaFromUw;

    @Column(name = "MSTE_TGL_KIRIM_GA_TO_ADMIN")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date tglKirimGaToAdmin;

    @Column(name = "MSTE_TGL_TERIMA_ADMIN_FROM_GA")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date tglTerimaAdminFromGa;

    @Column(name = "MSTE_TGL_KIRIM_ADMIN_TO_AGENT")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date tglKirimAdminToAgent;

    @Column(name = "MSTE_FLAG_SPECIAL_OFFER")
    private Integer flagSpecialOffer;

    @Column(name = "MSTE_FLAG_HADIAH")
    private Integer flagHadiah;

    @Column(name = "FLAG_PACKET")
    private Integer flagPacket;

    @Column(name = "MSTE_NEXT_BIAYA")
    @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
    private Date nextBiaya;

    @Column(name = "REG_SPAJ_PACKET")
    private String regSpajPacket;

    @Column(name = "MSTE_DTH")
    private Integer dth;

    @Column(name = "MSTE_GMIT")
    private Integer gmit;

    @Column(name = "LSSA_ID_BAS")
    private Integer lssaIdBas;

    @Column(name = "FLAG_SPEEDY")
    private Integer flagSpeedy;

    @Column(name = "JNS_MEDIS")
    private Integer jnsMedis;

    @Column(name = "MSTE_NO_HP")
    private String noHp;

    @Column(name = "HEALTH_QUEST")
    private Integer healthQuest;

    @Column(name = "FLAG_APPROVE_UW")
    private Integer flagApproveUw;

    @Column(name = "FLAG_APPROVE_COLLECTION")
    private Integer flagApproveCollection;

    @Column(name = "MSTE_NO_JM_UW")
    private String noJmUw;

    @Column(name = "JNS_MEDIS_CI")
    private Integer jnsMedisCi;

    @Column(name = "FLAG_SPICA")
    private Integer flagSpica;

    @Column(name = "LSSA_SUB_ID")
    private Integer lssaSubId;
}

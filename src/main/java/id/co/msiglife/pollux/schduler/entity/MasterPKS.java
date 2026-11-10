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
@Table(name = "MST_PKS_CL", schema = "EKA")
public class MasterPKS {

	@Id
	@Column(name = "PKS_ID")
	private String pksId;

	@Column(name = "NO_PKS")
	private String noPks;

	@Column(name = "MCL_ID")
	private String mclId;
	
	@Column(name = "INPUTDATE")
	@JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
	private Date inputDate;
	
	@Column(name = "LUS_ID")
	private Integer lusId;
	
	@Column(name = "TGL_PKS")
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
	private Date tglPks;

	@Column(name = "TGL_TERIMA_PKS_FINAL")
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
	private Date tglPksFinal;
	
	@Column(name = "PKS_ENDDATE")
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
	private Date endDate;
	
	@Column(name = "DOKUMENFINAL")
	private Integer dokFinal;

	@Column(name = "REKENING")
	private String rekening;
	
	@Column(name = "ATAS_NAMA")
	private String atasNama;
	
	@Column(name = "NAMA_KEYPERSON")
	private String nmKeyPerson;
	
	@Column(name = "JABATAN_KEYPERSON")
	private String jabKeyPerson;
	
	@Column(name = "KONTAK_KEYPERSON")
	private String ktkKeyPerson;
	
	@Column(name = "EMAIL")
	private String email;
	
	@Column(name = "ALAMAT_PUSAT")
	private String alamatPusat;

	@Column(name = "LSSB_KODE")
	private Integer lssbKode;
	
	@Column(name = "NAMA_SUMBERBISNIS")
	private String nmSb;
	
	@Column(name = "ALAMAT_SUMBERBISNIS")
	private String alamatSb;
	
	@Column(name = "PIC_SUMBERBISNIS")
	private String picSb;
	
	@Column(name = "PICJABATAN_SUMBERBISNIS")
	private String jabSb;
	
	@Column(name = "PICKONTAK_SUMBERBISNIS")
	private String ktkSb;
	
	@Column(name = "FLAG_AKTIF")
	private Integer flagAktif;

	@Column(name="TAX_TYPE_PLX")
	private Integer taxType;

	@Column(name = "PIC_BANK_EMAIL_PLX")
	private String picBankEmailPlx;

}

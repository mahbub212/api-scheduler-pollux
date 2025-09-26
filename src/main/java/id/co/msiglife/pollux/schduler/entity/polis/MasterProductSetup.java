package id.co.msiglife.pollux.schduler.entity.polis;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@Table(name = "MST_PRODSETUP_CL", schema = "EKA")
public class MasterProductSetup {

	@Id
	@Column(name = "PRODUCT_ID")
	private String productSetupId;

	@Column(name = "LSBS_ID")
	private Integer productId;

	@Column(name = "LSDBS_NUMBER")
	private Integer productNumber;

	@Column(name = "INPUTDATE")
	@JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
	private Date inputDate;

	@Column(name = "LUS_ID")
	private Integer lusId;

	@Column(name = "LINI_BISNIS")
	private Integer lineBisnis;

	@Column(name = "FLAG_USIA")
	private Integer flagUsia;

	@Column(name = "FLAG_RATE_TAHUNAN")
	private Integer flagRateTahunan;

	@Column(name = "FLAG_RATE_BULANAN")
	private Integer flagRateBulanan;

	@Column(name = "TIPE_JURNAL")
	private Integer tipeJurnal;

	@Column(name = "PERSEN_FEEBASED")
	private Double persenFeebased;

	@Column(name = "KOMISI_BROKER")
	private Double komisiBroker;

	@Column(name = "JENIS_CETAK")
	private Integer jenisCetak;

	@Column(name = "OUTSOURCE_PRINT")
	private Integer outsourcePrint;

	@Column(name = "OUTSOURCE_PRINT_FORMAT")
	private Integer outsourcePrintFormat;

	@Column(name = "KOMISI_EXTRA")
	private Integer komisiExtra;

	@Column(name = "TIPE_NT")
	private Integer tipeNt;

	@Column(name = "MINIMUM_PREMI")
	private Double minimumPremi;

	@Column(name = "INTEREST")
	private Double interest;

	@Column(name = "FLAG_JOINTLIFE")
	private Integer flagJointLife;

	@Column(name = "JENIS_UP")
	private Integer jenisUp;

	@Column(name = "JENIS_PAKET_MEDIS")
	private Integer jenisPaketMedis;

	@Column(name = "BIAYA_ADMIN")
	private Double biayaAdmin;

	@Column(name = "TIPE_SERTIFIKAT")
	private Integer tipeSertifikat;

	@Column(name = "TIPE_KPS")
	private Integer tipeKps;

	@Column(name = "FLAG_AKTIF")
	private Integer flagAktif;

	@Column(name = "PKS_ID")
	private String pksId;

	@Column(name = "LSBP_ID")
	private BigDecimal bankPusat;

	@Column(name = "POLIS_INDUK")
	private String polisInduk;

	@Column(name = "FLAG_FEEBASED")
	private Integer flagFeebased;

//	@Column(name = "TAX_TYPE_PLX")
//	private Integer taxType;

	// penambahan syariah start
	@Column(name = "FLAG_EMAIL_CERTIFICATE_PLX")
	private Integer flagEmailCertificate;

	@Column(name = "UJROH_PERSEN_PLX")
	private Double ujrohPersen;

	@Column(name = "TABARRU_PERSEN_PLX")
	private Double tabarruPersen;

	// penambahan syariah end
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "LSBS_ID", referencedColumnName = "LSBS_ID", insertable = false, updatable = false),
			@JoinColumn(name = "LSDBS_NUMBER", referencedColumnName = "LSDBS_NUMBER", insertable = false, updatable = false)
	})
	private ProductListDet productListDet;

}

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
@Table(name = "MST_BRANCHPARTNER_CL", schema = "EKA")
public class MasterBranchPartner {

	@Id
	@Column(name = "BRANCH_ID")
	private String branchId;

	@Column(name = "MCL_ID")
	private String mclId;
	
	@Column(name = "PKS_ID")
	private String pksId;
	
	@Column(name = "INPUTDATE")
	@JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
	private Date inputDate;
	
	@Column(name = "LUS_ID")
	private Integer lusId;
	
	@Column(name = "MSAG_ID")
	private String msagId;
	
	@Column(name = "EMAIL_CABANG")
	private String emailCab;
	
	@Column(name = "EMAIL_AO")
	private String emailAo;
	
	@Column(name = "NAMA_PENGIRIMAN")
	private String namaPengiriman;
	
	@Column(name = "ALAMAT_PENGIRIMAN")
	private String alamatPengiriman;

	@Column(name = "LSKA_ID")
	private Integer lskaId;
	
	@Column(name = "UP_DOKUMEN")
	private String upDokumen;
	
	@Column(name = "CONTACT_UP")
	private String contactUp;
	
	@Column(name = "FLAG_AKTIF")
	private Integer flagAktif;

	@Column(name = "AO_NAME")
	private String aoName;

	@Column(name = "LEADER_NAME")
	private String leaderName;

	@Column(name = "UPDATE_DATE")
	@JsonFormat(timezone = "Asia/Jakarta", shape = JsonFormat.Shape.STRING)
	private Date updateDate;

}

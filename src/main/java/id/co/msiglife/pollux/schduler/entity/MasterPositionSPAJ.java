package id.co.msiglife.pollux.schduler.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import id.co.msiglife.pollux.schduler.composite.CompMasterPositionSPAJ;
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
@IdClass(CompMasterPositionSPAJ.class)
@Table(name = "MST_POSITION_SPAJ", schema = "EKA")
public class MasterPositionSPAJ {

	@Id
	@Column(name = "REG_SPAJ")
	private String regSpaj;

	@Id
	@Column(name = "MSPS_DATE")
	@JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
	private Date positionDate;

	@Column(name = "LSPD_ID")
	private Integer lspdId;

	@Column(name = "LSSP_ID")
	private Integer lsspId;

	@Column(name = "LSSA_ID")
	private Integer lssaId;

	@Column(name = "LUS_ID")
	private Integer lusId;

	@Column(name = "MSPS_DESC")
	private String desc;

	@Column(name = "SUB_ID")
	private Integer subId;

	@Column(name = "LSPD_BEFORE")
	private Integer lspdBefore;

	@Column(name = "JENIS")
	private String jenis;

	@Column(name = "LSSA_ID_BAS")
	private Integer lssaIdBas;

	@Column(name = "SUB_ID_BAS")
	private Integer subIdBas;

}
package id.co.msiglife.pollux.schduler.dto.surrender;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import id.co.msiglife.pollux.schduler.util.StaticValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Lob;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListUWHistoryApprovalDTO {

    @JsonProperty("status_id")
    private Long approvalHistId;

    @JsonProperty("nomor_spaj")
    private String regSpaj;

    @JsonProperty("no_policy")
    private String noPolicy;

    @JsonProperty("msuw_id")
    private Long msuwId;

    @JsonProperty("role_id")
    private Integer roleId;

    @JsonProperty("is_approved")
    private Boolean isApproved;

    @JsonProperty("decision")
    private String decision;

    @JsonProperty("user_id")
    private Integer lusId;

    @JsonProperty("remark")
    private String remark;

    @JsonProperty("created_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = StaticValue.ISO_DATETIME_FORMAT, timezone = StaticValue.TIMEZONE_APP)
    private Date createdDate;

    @JsonProperty("approved_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = StaticValue.ISO_DATETIME_FORMAT, timezone = StaticValue.TIMEZONE_APP)
    private Date approvedDate;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("role_name")
    private String roleName;

    @JsonProperty("pemegang_polis")
    private String pemegangPolis;

    @JsonProperty("nama_tertanggung")
    private String namaTertanggung;

    @JsonProperty("product")
    private String product;

    @JsonProperty("uang_pertanggungan")
    private BigDecimal uangPertanggungan;

    @JsonProperty("keputusan")
    private String keputusan;

    @JsonProperty("final_remark")
    private String finalRemark;

    @JsonProperty("total_refund")
    private BigDecimal totalRefund;

    @JsonProperty("reason_of_refund")
    private String reasonOfRefund;

    @JsonProperty("reason_surrender")
    private String reasonSurrender;

    @JsonProperty("nama_cabang")
    private String namaCabang;
}
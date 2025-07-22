package id.co.msiglife.pollux.schduler.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@Table(name = "MST_WF_REQUEST", schema = "EKA")
public class MasterWfRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MSWF_REQUEST_ID", insertable = false, updatable = false)
    private Integer requestId;

    @Column(name = "MSWF_REQUEST_DET")
    private String requestDet;

    @Column(name = "MSWF_PARENT_DOC_ID")
    private Long parentDocId;

    @Column(name = "MSWF_DOC_ID")
    private Long docId;

    @Column(name = "LSWF_CONFIG_ID")
    private Integer lswfConfigId;

    @Column(name = "LSWF_CONFIG_ACTION")
    private String lswfConfigAction;

    @Column(name = "LSWF_TYPE_ID")
    private Integer lswfTypeId;

    @Column(name = "LSWF_STATE_ID_FROM")
    private Integer lswfStateIdFrom;

    @Column(name = "MSWF_REQUEST_CREATED_DATE")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date requestCreatedDate;
}

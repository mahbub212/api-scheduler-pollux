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
@Table(name = "MST_WF_DOCUMENT_HISTORY", schema = "EKA")
public class MasterWFDocumentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MSWF_DOC_HIST_ID", insertable = false, updatable = false)
    private Long docHistId;

    @Column(name = "LSWF_TYPE_ID")
    private Integer lswfTypeId;

    @Column(name = "MSWF_PARENT_DOC_ID")
    private Long parentDocId;

    @Column(name = "MSWF_DOC_ID")
    private Long docId;

    @Column(name = "LSWF_STATE_ID_FROM")
    private Integer lswfStateIdFrom;

    @Column(name = "LSWF_CONFIG_ACTION")
    private String lswfConfigAction;

    @Column(name = "LUS_ID")
    private Integer lusId;

    @Column(name = "LSWF_STATE_ID_TO")
    private Integer lswfStateIdTo;

    @Column(name = "MSWF_DOC_HIST_CREATED")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date docHistCreated;

    @Column(name = "MSWF_DOC_STATUS_KEY")
    private String docStatusKey;

    @Column(name = "MSWF_DOC_STATUS_VALUE")
    private Integer docStatusValue;

    @Column(name = "MSWF_DOC_HIST_REMARKS")
    private String docHistRemarks;

    @Column(name = "MSWF_DOC_NUMBER")
    private String docNumber;

    @Column(name = "LSWF_STATUS_ID")
    private Integer lswfStatusId;

    @Lob
    @Column(name = "OTHER_PARAMETERS", columnDefinition = "CLOB")
    private String otherParameters;
}

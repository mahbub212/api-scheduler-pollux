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
@Table(name = "MST_WF_DOCUMENT", schema = "EKA")
public class MasterWFDocument {

    @Column(name = "MSWF_PARENT_DOC_ID")
    private Long parentDocId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MSWF_DOC_ID", insertable = false, updatable = false)
    private Long docId;

    @Column(name = "LSWF_TYPE_ID")
    private Integer lswfTypeId;

    @Column(name = "MSWF_DOC_NUMBER")
    private String docNumber;

    @Column(name = "LSWF_STATE_ID")
    private Integer lswfStateId;

    @Column(name = "MSWF_DOC_CREATED")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date docCreated;

    @Column(name = "MSWF_DOC_UPDATED")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date docUpdated;

    @Column(name = "LUS_ID")
    private Integer lusId;

    @Column(name = "MSWF_DOC_PRIORITY")
    private Integer docPriority;

    @Column(name = "LSWF_STATUS_ID")
    private Integer lswfStatusId;

    @Column(name = "MSWF_DOC_FURTHER_BY")
    private Integer docFurtherBy;

}


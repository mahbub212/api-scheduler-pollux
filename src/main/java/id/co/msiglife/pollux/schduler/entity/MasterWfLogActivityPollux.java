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
@Table(name = "MST_WF_LOG_ACTIVITY_PLX", schema = "EKA")
public class MasterWfLogActivityPollux {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOG_ID", insertable = false, updatable = false)
    private Long logId;

    @Column(name = "IP_ADDRESS")
    private String ipAddress;

    @Column(name = "ENDPOINT_URL")
    private String endpointUrl;

    @Column(name = "METHOD")
    private String method;

    @Column(name = "REQ_QUERY")
    private String requestQuery;

    @Column(name = "REQ_BODY")
    private String requestBody;

    @Column(name = "RESPONSE")
    private String response;

    @Column(name = "SUCCESS")
    private Boolean success;

    @Column(name = "LOG_DATE")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date logDate;

    @Column(name = "DOC_NUMBER")
    private String docNumber;

    @Column(name = "LUS_ID")
    private Integer userId;
}

package id.co.msiglife.pollux.schduler.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "MST_EMAIL", schema = "EKA")
public class MasterEmail {

    @Id
    @Column(name = "ME_ID")
    private String Id;

    @Column(name = "ME_SYSTEM")
    private String system;

    @Column(name = "ME_SHOW_SYSTEM")
    private int showSystem;

    @Column(name = "ME_SHOW_FOOTER")
    private int showFooter;

    @Column(name = "ME_FROM")
    private String from;

    @Column(name = "ME_FROM_ALIAS")
    private String fromAlias;

    @Column(name = "ME_TO")
    private String to;

    @Column(name = "ME_CC")
    private String cc;

    @Column(name = "ME_BCC")
    private String bcc;

    @Column(name = "ME_SUBJECT")
    private String subject;

    @Lob
    @Column(name = "ME_MESSAGE", columnDefinition = "CLOB")
    private String message;

    @Column(name = "MEC_ID")
    private int mecId;

    @Column(name = "ME_HTML")
    private int html;

    @Column(name = "ME_STATUS")
    private int status;

    @Column(name = "ME_STATUS_MESSAGE")
    private String statusMessage;

    @Column(name = "ME_COUNT")
    private int count;

    @Column(name = "ME_LUS_ID")
    private Integer userId;

    @Column(name = "ME_INPUT_DATE")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date inputDate;

    @Column(name = "ME_SENT_DATE")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date sentDate;

    @Column(name = "PRIORITY")
    private int priority;

    @Column(name = "ME_SCHEDULER_DATE")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date schedulerDate;

    @Column(name = "ME_JENIS_SURAT")
    private Integer jenisSurat;

    @Column(name = "ME_REG_SPAJ")
    private String regSpaj;

    @Column(name = "JENIS_ID")
    private int jenisId;

    @Column(name = "NO_TRX")
    private String noTrx;

    @Column(name = "FLAG_NEW")
    private Integer flagNew;

    @Column(name = "PATH_ATTACH")
    private String pathAttach;

}
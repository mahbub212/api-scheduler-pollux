package id.co.msiglife.pollux.schduler.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import id.co.msiglife.pollux.schduler.composite.CompMasterUploadDokumen;
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
@IdClass(CompMasterUploadDokumen.class)
@Table(name = "MST_UPLOAD_DOKUMEN_PLX", schema = "EKA")
public class MasterUploadDokumen {

    @Id
    @Column(name = "REG_SPAJ")
    private String regSpaj;

    @Id
    @Column(name = "DOKUMEN_ID")
    private Integer dokumenId;

    @Column(name = "LUS_ID")
    private Integer lusId;

    @Column(name = "JNS_DOKUMEN_ID")
    private Integer jenisDokumenId;

    @Column(name = "PATH_FILE")
    private String pathFile;

    @Column(name = "UPLOAD_CREATED_DATE")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date uploadCreatedDate;

    @Column(name = "UPLOAD_UPDATED_DATE")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date uploadUpdatedDate;
    
}

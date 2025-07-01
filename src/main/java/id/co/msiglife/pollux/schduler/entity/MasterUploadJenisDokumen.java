package id.co.msiglife.pollux.schduler.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MST_UPLOAD_JENIS_DOKUMEN_PLX", schema = "EKA")
public class MasterUploadJenisDokumen {

    @Id
    @Column(name = "JNS_DOKUMEN_ID")
    private Integer jenisDokumenId;

    @Column(name = "KODE_DOKUMEN")
    private String kodeDokumen;

    @Column(name = "NAMA_DOKUMEN")
    private String namaDokumen;
    
}

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
@Table(name = "MST_PERSON", schema = "EKA")
public class MasterPerson {

    @Id
    @Column(name = "MCL_ID")
    private String mclId;

    @Column(name = "MCL_GELAR")
    private String mclGelar;

    @Column(name = "LSIDE_ID")
    private Integer lsideId;

    @Column(name = "MSPE_NO_IDENTITY")
    private String noIdentity;

    @Column(name = "LSED_ID")
    private Integer lsedId;

    @Column(name = "MST_PERSH_ID")
    private String mstPershId;

    @Column(name = "LGJ_ID")
    private String lgjId;

    @Column(name = "LJB_ID")
    private String ljbId;

    @Column(name = "MPN_JOB_DESC")
    private String mpnJobDesc;

    @Column(name = "MSPE_MOTHER")
    private String mother;

    @Column(name = "MSPE_SEX")
    private Integer sex;

    @Column(name = "MSPE_DATE_BIRTH")
    @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
    private Date dateBirth;

    @Column(name = "MSPE_PLACE_BIRTH")
    private String placeBirth;

    @Column(name = "LSAG_ID")
    private Integer lsagId;

    @Column(name = "MSPE_STS_MRT")
    private String stsMrt;

    @Column(name = "LSNE_ID")
    private Integer lsneId;

    @Column(name = "MSPE_PICTURE")
    private String picture;

    @Column(name = "MSPE_EMAIL")
    private String email;

    @Column(name = "MSPE_LAMA_KERJA")
    private Integer lamaKerja;

    @Column(name = "LST_LSNE_ID")
    private Integer lstLsneId;

    @Column(name = "LST_LSAG_ID")
    private Integer lstLsagId;

    @Column(name = "LST_LSED_ID")
    private Integer lstLsedId;

    @Column(name = "MSPE_TUJUAN")
    private String tujuan;

    @Column(name = "MSPE_PENGHASILAN")
    private String penghasilan;

    @Column(name = "MSPE_PENDANAAN")
    private String pendanaan;

    @Column(name = "MSPE_SMBR_PENGHASILAN")
    private String smbrPenghasilan;

    @Column(name = "MSPE_NO_HP")
    private String noHp;

    @Column(name = "MSPE_NPWP")
    private String npwp;

    @Column(name = "MSPE_KERJA")
    private String kerja;
}

package id.co.msiglife.pollux.schduler.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MST_ADDRESS_NEW", schema = "EKA")
public class MasterAddressNew {

    @Id
    @Column(name = "MCL_ID")
    private String mclId;

    @Column(name = "ALAMAT_RUMAH")
    private String alamatRumah;

    @Column(name = "KD_POS_RUMAH")
    private String kdPosRumah;

    @Column(name = "KOTA_RUMAH")
    private String kotaRumah;

    @Column(name = "AREA_CODE_RUMAH")
    private String areaCodeRumah;

    @Column(name = "TELPON_RUMAH")
    private String telponRumah;

    @Column(name = "ALAMAT_KANTOR")
    private String alamatKantor;

    @Column(name = "KD_POS_KANTOR")
    private String kdPosKantor;

    @Column(name = "KOTA_KANTOR")
    private String kotaKantor;

    @Column(name = "AREA_CODE_KANTOR")
    private String areaCodeKantor;

    @Column(name = "TELPON_KANTOR")
    private String telponKantor;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "NO_HP")
    private String noHp;

    @Column(name = "NO_HP2")
    private String noHp2;

    @Column(name = "NO_FAX")
    private String noFax;

    @Column(name = "AREA_CODE_RUMAH2")
    private String areaCodeRumah2;

    @Column(name = "TELPON_RUMAH2")
    private String telponRumah2;

    @Column(name = "AREA_CODE_KANTOR2")
    private String areaCodeKantor2;

    @Column(name = "TELPON_KANTOR2")
    private String telponKantor2;

    @Column(name = "AREA_CODE_FAX")
    private String areaCodeFax;

    @Column(name = "ALAMAT_TPT_TINGGAL")
    private String alamatTptTinggal;

    @Column(name = "KOTA_TPT_TINGGAL")
    private String kotaTptTinggal;

    @Column(name = "KD_POS_TPT_TINGGAL")
    private String kdPosTptTinggal;

    @Column(name = "LSPR_ID")
    private Integer lsprId;

    @Column(name = "AREA_CODE_3RD")
    private String areaCode3rd;

    @Column(name = "TELPON_3RD")
    private String telpon3rd;

    @Column(name = "ALAMAT_3RD")
    private String alamat3rd;

    @Column(name = "AREA_CODE_KANTOR_3RD")
    private String areaCodeKantor3rd;

    @Column(name = "TELPON_KANTOR_3RD")
    private String telponKantor3rd;

    @Column(name = "EMAIL_3RD")
    private String email3rd;

    @Column(name = "LSKA_ID")
    private Integer lskaId;

    @Column(name = "LSKC_ID")
    private Integer lskcId;

    @Column(name = "LSKL_ID")
    private Integer lsklId;

    @Column(name = "LSPR_ID_KANTOR")
    private Integer lsprIdKantor;

    @Column(name = "LSKA_ID_KANTOR")
    private Integer lskaIdKantor;

    @Column(name = "LSKC_ID_KANTOR")
    private Integer lskcIdKantor;

    @Column(name = "LSKl_ID_KANTOR")
    private Integer lsklIdKantor;

    @Column(name = "LSPR_ID_OTHER")
    private Integer lsprIdOther;

    @Column(name = "LSKA_ID_OTHER")
    private Integer lskaIdOther;

    @Column(name = "LSKC_ID_OTHER")
    private Integer lskcIdOther;

    @Column(name = "LSKL_ID_OTHER")
    private Integer lsklIdOther;

    @Column(name = "NEGARA_TPT_TINGGAL")
    private Integer negaraTptTinggal;

    @Column(name = "NEGARA_RUMAH")
    private Integer negaraRumah;

    @Column(name = "NEGARA_KANTOR")
    private Integer negaraKantor;

    @Column(name = "NO_HP3")
    private String noHp3;

    @Column(name = "RT")
    private String rt;

    @Column(name = "RW")
    private String rw;

    @Column(name = "RT_TPT_TINGGAL")
    private String rtTptTinggal;

    @Column(name = "RW_TPT_TINGGAL")
    private String rwTptTinggal;
}

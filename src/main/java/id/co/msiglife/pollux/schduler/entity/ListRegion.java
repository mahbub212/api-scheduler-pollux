package id.co.msiglife.pollux.schduler.entity;
import id.co.msiglife.pollux.schduler.composite.CompListRegion;


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
@IdClass(CompListRegion.class)
@Table(name = "LST_REGION", schema = "EKA")
public class ListRegion {

    @Id
    @Column(name = "LCA_ID")
    private String lcaId;

    @Id
    @Column(name = "LWK_ID")
    private String lwkId;

    @Id
    @Column(name = "LSRG_ID")
    private String lsrgId;
    
    @Column(name = "LSRG_NAMA")
    private String nama;

    @Column(name = "LSRG_ALAMAT")
    private String alamat;

    @Column(name = "LAR_ID")
    private Integer larId;

    @Column(name = "TEAM_NAME")
    private String teamName;

    @Column(name = "LSRG_FLAG")
    private Integer flag;

    @Column(name = "KD_SDM")
    private String kdSdm;

    @Column(name = "KD_ADS")
    private String kdAds;

    @Column(name = "LAN_ID")
    private String lanId;

    @Column(name = "FLAG_AAJI")
    private Integer flagAaji;

    @Column(name = "LADC_ID")
    private String ladcId;

    @Column(name = "LADC_LEAD")
    private String ladcLead;

    @Column(name = "AAM_ID")
    private String aamId;

    @Column(name = "RAD_ID")
    private String radId;

    @Column(name = "COA_ID")
    private String coaId;

    @Column(name = "ID_KP")
    private String idKp;

    @Column(name = "ID_WIL")
    private Integer idWil;

    @Column(name = "MSAG_ADB")
    private String msagAdb;

    @Column(name = "LSRG_PKP")
    private Integer pkp;

    @Column(name = "MMS_ID")
    private String mmsId;

    @Column(name = "MCS_ID")
    private String mcsId;

}

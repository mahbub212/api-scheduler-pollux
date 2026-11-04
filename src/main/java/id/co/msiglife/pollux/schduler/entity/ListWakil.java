package id.co.msiglife.pollux.schduler.entity;

import id.co.msiglife.pollux.schduler.composite.CompListWakil;
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
@IdClass(CompListWakil.class)
@Table(name = "LST_WAKIL", schema = "EKA")
public class ListWakil {

    @Id
    @Column(name = "LCA_ID")
    private String lcaId;

    @Id
    @Column(name = "LWK_ID")
    private String lwkId;
    
    @Column(name = "LWK_NAMA")
    private String nama;

    @Column(name = "LWK_KD_POLIS")
    private String kdPolis;

    @Column(name = "LWK_ADMIN")
    private String admin;

    @Column(name = "LWK_TELP")
    private String telp;

    @Column(name = "LWK_FLAG")
    private Integer flag;

    @Column(name = "MRMC_ID")
    private String mrmcId;

    @Column(name = "FLAG_PRINT_CABANG")
    private Integer flagPrintCabang;

    @Column(name = "LWK_FLAG2")
    private Integer flag2;

    @Column(name = "ID_COMP")
    private Double idComp;

    @Column(name = "AKM_FLAG")
    private Integer akmFlag;

    @Column(name = "ID_AD")
    private String idAd;

    @Column(name = "ID_RM")
    private String idRm;

    @Column(name = "SYARIAH")
    private Integer syariah;

    @Column(name = "ID_RD")
    private String idRd;

    @Column(name = "LWP_ID")
    private Integer lwpId;
    
}

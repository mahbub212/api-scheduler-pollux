package id.co.msiglife.pollux.schduler.entity.polis;

import id.co.msiglife.pollux.schduler.composite.CompProductListDet;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(CompProductListDet.class)
@Data
@Table(name = "LST_DET_BISNIS", schema = "EKA")
public class ProductListDet {

    @Id
    @Column(name = "LSBS_ID")
    private Integer productId;

    @Id
    @Column(name = "LSDBS_NUMBER")
    private Integer productNumber;

    @Column(name = "LSDBS_NAME")
    private String productName;

    @Column(name = "LSDBS_REAS_CLIENT")
    private Integer reasClient;

    @Column(name = "LSDBS_MAX_CLAIM")
    private Integer maxClaim;
    
    @Column(name = "LSDBS_LAMA_BAYAR")
    private Integer lamaBayar;

    @Column(name = "LSDBS_URUT")
    private Integer urut;

    @Column(name = "LSDBS_ASO")
    private Integer aso;

    @Column(name = "LSDBS_DISC")
    private Double disc;

    @Column(name = "LSDBS_TABROH")
    private Double tabroh;

    @Column(name = "LSDBS_UJROH")
    private Double ujroh;

    @Column(name = "LSDBS_BIAYA_ADMIN")
    private Double biayaAdmin;

    @Column(name = "LSDBS_AKTIF")
    private Integer aktif;

    @Column(name = "LSDBS_GROUP_REPORT")
    private Integer groupReport;

    @Column(name = "LSDBS_TABLE_RATE")
    private Integer tableRate;

    @Column(name = "LSDBS_SPAJ")
    private String spaj;

    @Column(name = "LSDBS_GROUP_NAME")
    private String groupName;

    @Column(name = "JN_BANK")
    private Integer bank;

    @Column(name = "KODEX")
    private String kodex;

    @Column(name = "LSDBS_NAME_SUB")
    private String nameSub;

    @Column(name = "LSDBS_GRP_CLAIM")
    private String grpClaim;

    @Column(name = "LSDBS_PAJAK")
    private Integer pajak;

    @Column(name = "LSDBS_UP_TETAP")
    private Double upTetap;

    @Column(name = "LSDBS_UP_MENURUN")
    private Double upMenurun;

    @Column(name = "LSDBS_NAME_SYARIAH")
    private String nameSyariah;

    @Column(name = "LSDBS_NAME0")
    private String name0;

    @Column(name = "LSDBS_NAME_SYARIAH0")
    private String nameSyariah0;

    @Column(name = "LSDBS_VIEW")
    private String view;

    @Column(name = "LSDBS_VIEW0")
    private String view0;

    @Column(name = "LSDBS_VIEW_SYARIAH")
    private String viewSyariah;

    @Column(name = "LSDBS_VIEW_SYARIAH0")
    private String viewSyariah0;

    @Column(name = "LSDBS_DEPKEU")
    private String depkeu;

    @Column(name = "LSDBS_DEPKEU0")
    private String depkeu0;

    @Column(name = "LSBS_DISC_PESERTA")
    private Integer discPeserta;

    @Column(name = "LSDBS_DEATH")
    private Integer death;

    @Column(name = "LSDBS_BENEFIT_TYPE")
    private Integer benefitType;

    @Column(name = "LSDBS_CHECK")
    private Integer check;

    @Column(name = "MPI_MOU")
    private String mpiMou;

    @Column(name = "LSDBS_JN_PLAN")
    private Integer jnPlan;

    @Column(name = "LSDBS_NAME_BANCASS")
    private String nameBancass;

    @Column(name = "FLAG_PROPOSAL_MOBILE")
    private Integer flagProposalMobile;

    @Column(name = "SPAJ_TYPE")
    private Integer spajType;

    @Column(name = "FLAG_PAY_MODE")
    private Integer flagPayMode;

    @Column(name = "LSDBS_JENIS_PESERTA")
    private Integer jenisPeserta;

    @Column(name = "LSDBS_NAME_DISPLAY")
    private String nameDisplay;

    @Column(name = "FLAG_CLEAN")
    private Integer flagClean;

    @Column(name = "LSDBS_NAME_POLIS")
    private String namePolis;

    @Column(name = "LSDBS_NAME_SUB_PLAN")
    private String nameSubPlan;

    @Column(name = "RISK_PA")
    private String riskPa;

    @Column(name = "LSDBS_NAME_DISPLAY_SYARIAH")
    private String nameDisplaySyariah;

}

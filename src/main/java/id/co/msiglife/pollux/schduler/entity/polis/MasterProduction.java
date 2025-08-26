package id.co.msiglife.pollux.schduler.entity.polis;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "MST_PRODUCTION", schema = "EKA")
@Data
public class MasterProduction {

    @Column(name = "REG_SPAJ")
    private String regSpaj;

    @Column(name = "APE_KONTES")
    private BigDecimal apeKontes;

    @Column(name = "APE_EVA")
    private BigDecimal apeEva;

    @Column(name = "APE_PROD")
    private BigDecimal apeProd;

    @Column(name = "MSPRO_NILAI_KURS")
    private BigDecimal msproNilaiKurs;

    @Column(name = "TGL_INPUT")
    private Date tglInput;

    @Column(name = "UPP_EVA")
    private BigDecimal uppEva;

    @Column(name = "MSPRO_PROD_KE")
    private Integer msproProdKe;

    @Id
    @Column(name = "MSPRO_PROD_DATE")
    private Date msproProdDate;

    @Column(name = "MSBI_TAHUN_KE")
    private Integer msbiTahunKe;

    @Column(name = "MSBI_PREMI_KE")
    private Integer msbiPremiKe;

    @Column(name = "MSPRO_JN_PROD")
    private String msproJnProd;

    @Column(name = "LKU_ID")
    private String lkuId;

    @Column(name = "MSPRO_BEG_DATE")
    private Date msproBegDate;

    @Column(name = "MSPRO_END_DATE")
    private Date msproEndDate;

    @Column(name = "MSPRO_POLICY_COST")
    private BigDecimal msproPolicyCost;

    @Column(name = "MSPRO_CARD_COST")
    private BigDecimal msproCardCost;

    @Column(name = "MSPRO_STAMP")
    private BigDecimal msproStamp;

    @Column(name = "MSPRO_JML_MEMBER")
    private Integer msproJmlMember;

    @Column(name = "LCA_ID")
    private String lcaId;

    @Column(name = "LWK_ID")
    private String lwkId;

    @Column(name = "LSRG_ID")
    private Long lsrgId;

    @Column(name = "MSPRO_MSAG_EB")
    private String msproMsagEb;

    @Column(name = "UPP_KONTES")
    private BigDecimal uppKontes;

    @Column(name = "UPP_LAIN")
    private BigDecimal uppLain;

    @Column(name = "COM_EVA")
    private BigDecimal comEva;

    @Column(name = "UPP_BONUS")
    private BigDecimal uppBonus;

    @Column(name = "LSTB_ID")
    private Integer lstbId;

    @Column(name = "AGENT_1")
    private String agent1;

    @Column(name = "AGENT_2")
    private String agent2;

    @Column(name = "AGENT_3")
    private String agent3;

    @Column(name = "AGENT_4")
    private String agent4;

    @Column(name = "NO_PRE")
    private String noPre;

    @Column(name = "NO_JM")
    private String noJm;

    @Column(name = "AGENT_5")
    private String agent5;

    @Column(name = "APE_KOMPENSASI")
    private BigDecimal apeKompensasi;
}


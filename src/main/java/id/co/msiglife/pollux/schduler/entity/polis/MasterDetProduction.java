package id.co.msiglife.pollux.schduler.entity.polis;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "MST_DET_PRODUCTION", schema = "EKA")
@Data
public class MasterDetProduction {

    @Column(name = "REG_SPAJ")
    private String regSpaj;

    @Column(name = "MSDPR_URUT")
    private Integer msdprUrut;

    @Column(name = "LDP_PLAN")
    private String ldpPlan;

    @Column(name = "LSTB_ID")
    private Integer lstbId;

    @Id
    @Column(name = "MSPRO_PROD_KE")
    private Integer msproProdKe;

    @Column(name = "MSPRO_PROD_DATE")
    private LocalDate msproProdDate;

    @Column(name = "LSBS_ID")
    private Integer lsbsId;

    @Column(name = "LSDBS_NUMBER")
    private Integer lsdbsNumber;

    @Column(name = "MSBI_TAHUN_KE")
    private Integer msbiTahunKe;

    @Column(name = "MSBI_PREMI_KE")
    private Integer msbiPremiKe;

    @Column(name = "MSDPR_PREMIUM")
    private BigDecimal msdprPremium;

    @Column(name = "MSDPR_DISCOUNT")
    private BigDecimal msdprDiscount;

    @Column(name = "MSDPR_TSI")
    private BigDecimal msdprTsi;
}


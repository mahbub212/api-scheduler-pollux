package id.co.msiglife.pollux.schduler.entity.polis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mst_det_billing")
public class MasterDetBilling {

    @Id
    @Column(name = "REG_SPAJ")
    private String regSpaj;

    @Column(name = "LDP_PLAN")
    private String ldpPlan;

    @Column(name = "MSBI_TAHUN_KE")
    private Integer msbiTahunKe;

    @Column(name = "MSBI_PREMI_KE")
    private Integer msbiPremiKe;

    @Column(name = "MSDB_DET_KE")
    private Integer msdbDetKe;

    @Column(name = "LSBS_ID")
    private Integer lsbsId;

    @Column(name = "LSDBS_NUMBER")
    private Integer lsdBsNumber;

    @Column(name = "MSDB_PREMIUM")
    private BigDecimal msdbPremium;

    @Column(name = "MSDB_DISCOUNT")
    private BigDecimal msdbDiscount;
}

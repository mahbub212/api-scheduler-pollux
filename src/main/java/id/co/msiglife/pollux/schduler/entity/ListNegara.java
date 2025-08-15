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
@Table(name = "LST_NEGARA", schema = "EKA")
public class ListNegara {

    @Id
    @Column(name = "LSNE_ID")
    private Integer lsneId;

    @Column(name = "LSNE_NOTE")
    private String note;

    @Column(name = "LSNE_KD_NEGARA")
    private String kdNegara;

    @Column(name = "LSNE_URUT")
    private Integer urut;

    @Column(name = "LSNE_OFAC_COUNTRY")
    private String ofacCountry;

    @Column(name = "LSNE_HIGH_RISK_COUNTRY")
    private Integer highRiskCountry;
}

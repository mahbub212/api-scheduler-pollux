package id.co.msiglife.pollux.schduler.entity;

import id.co.msiglife.pollux.schduler.composite.CompListPromotionPackage;
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
@Table(name = "LST_PROMOTION_PACKAGE", schema = "EKA")
@IdClass(CompListPromotionPackage.class)
public class ListPromotionPackage {

    @Id
    @Column(name = "REFERENCE_NO")
    private String referenceNo;

    @Id
    @Column(name = "NO_URUT")
    private Integer noUrut;

    @Column(name = "PROMOTION_DESC")
    private String promotionDesc;

    @Column(name = "FLAG_SELECTED")
    private Integer flagSelected;
}

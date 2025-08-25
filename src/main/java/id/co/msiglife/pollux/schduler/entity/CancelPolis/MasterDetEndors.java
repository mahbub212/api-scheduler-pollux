package id.co.msiglife.pollux.schduler.entity.CancelPolis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MST_DET_ENDORS", schema = "EKA")
public class MasterDetEndors {

    @Id
    @Column(name = "MSEN_ENDORS_NO", length = 12)
    private String msenEndorsNo;

    @Column(name = "LSJE_ID", precision = 22, scale = 0)
    private Long lsjeId;

    @Column(name = "MSTE_INSURED_NO", precision = 22, scale = 0)
    private Long msteInsuredNo;

    @Column(name = "MSDE_REINS_UPDATE", precision = 22, scale = 0)
    private Long msdeReinsUpdate;

    @Column(name = "MSENF_NUMBER", precision = 22, scale = 0, nullable = false)
    private Long msenfNumber;

    @Column(name = "MSDE_OLD1",  length = 700) private String msdeOld1;
    @Column(name = "MSDE_OLD2",  length = 500) private String msdeOld2;
    @Column(name = "MSDE_OLD3",  length = 700) private String msdeOld3;
    @Column(name = "MSDE_OLD4",  length = 700) private String msdeOld4;
    @Column(name = "MSDE_OLD5",  length = 700) private String msdeOld5;
    @Column(name = "MSDE_OLD6",  length = 700) private String msdeOld6;
    @Column(name = "MSDE_OLD7",  length = 700) private String msdeOld7;
    @Column(name = "MSDE_OLD8",  length = 700) private String msdeOld8;
    @Column(name = "MSDE_OLD9",  length = 700) private String msdeOld9;
    @Column(name = "MSDE_OLD10", length = 700) private String msdeOld10;
    @Column(name = "MSDE_OLD11", length = 700) private String msdeOld11;
    @Column(name = "MSDE_OLD12", length = 700) private String msdeOld12;
    @Column(name = "MSDE_OLD13", length = 700) private String msdeOld13;
    @Column(name = "MSDE_OLD14", length = 700) private String msdeOld14;
    @Column(name = "MSDE_OLD15", length = 700) private String msdeOld15;
    @Column(name = "MSDE_OLD16", length = 700) private String msdeOld16;
    @Column(name = "MSDE_OLD17", length = 700) private String msdeOld17;
    @Column(name = "MSDE_OLD18", length = 700) private String msdeOld18;
    @Column(name = "MSDE_OLD19", length = 700) private String msdeOld19;
    @Column(name = "MSDE_OLD20", length = 700) private String msdeOld20;
    @Column(name = "MSDE_OLD21", length = 700) private String msdeOld21;
    @Column(name = "MSDE_OLD22", length = 700) private String msdeOld22;
    @Column(name = "MSDE_OLD23", length = 700) private String msdeOld23;
    @Column(name = "MSDE_OLD24", length = 700) private String msdeOld24;
    @Column(name = "MSDE_OLD25", length = 700) private String msdeOld25;
    @Column(name = "MSDE_OLD26", length = 700) private String msdeOld26;
    @Column(name = "MSDE_OLD27", length = 700) private String msdeOld27;
    @Column(name = "MSDE_OLD28", length = 700) private String msdeOld28;
    @Column(name = "MSDE_OLD29", length = 700) private String msdeOld29;
    @Column(name = "MSDE_OLD30", length = 700) private String msdeOld30;

    @Column(name = "MSDE_NEW1",  length = 700) private String msdeNew1;
    @Column(name = "MSDE_NEW2",  length = 500) private String msdeNew2;
    @Column(name = "MSDE_NEW3",  length = 700) private String msdeNew3;
    @Column(name = "MSDE_NEW4",  length = 700) private String msdeNew4;
    @Column(name = "MSDE_NEW5",  length = 700) private String msdeNew5;
    @Column(name = "MSDE_NEW6",  length = 700) private String msdeNew6;
    @Column(name = "MSDE_NEW7",  length = 700) private String msdeNew7;
    @Column(name = "MSDE_NEW8",  length = 700) private String msdeNew8;
    @Column(name = "MSDE_NEW9",  length = 700) private String msdeNew9;
    @Column(name = "MSDE_NEW10", length = 700) private String msdeNew10;
    @Column(name = "MSDE_NEW11", length = 700) private String msdeNew11;
    @Column(name = "MSDE_NEW12", length = 700) private String msdeNew12;
    @Column(name = "MSDE_NEW13", length = 700) private String msdeNew13;
    @Column(name = "MSDE_NEW14", length = 700) private String msdeNew14;
    @Column(name = "MSDE_NEW15", length = 700) private String msdeNew15;
    @Column(name = "MSDE_NEW16", length = 700) private String msdeNew16;
    @Column(name = "MSDE_NEW17", length = 700) private String msdeNew17;
    @Column(name = "MSDE_NEW18", length = 700) private String msdeNew18;
    @Column(name = "MSDE_NEW19", length = 700) private String msdeNew19;
    @Column(name = "MSDE_NEW20", length = 700) private String msdeNew20;
    @Column(name = "MSDE_NEW21", length = 700) private String msdeNew21;
    @Column(name = "MSDE_NEW22", length = 700) private String msdeNew22;
    @Column(name = "MSDE_NEW23", length = 700) private String msdeNew23;
    @Column(name = "MSDE_NEW24", length = 700) private String msdeNew24;
    @Column(name = "MSDE_NEW25", length = 700) private String msdeNew25;
    @Column(name = "MSDE_NEW26", length = 700) private String msdeNew26;
    @Column(name = "MSDE_NEW27", length = 700) private String msdeNew27;
    @Column(name = "MSDE_NEW28", length = 700) private String msdeNew28;
    @Column(name = "MSDE_NEW29", length = 700) private String msdeNew29;
    @Column(name = "MSDE_NEW30", length = 700) private String msdeNew30;

}

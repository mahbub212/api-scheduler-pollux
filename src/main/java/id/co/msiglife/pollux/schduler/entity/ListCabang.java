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
@Table(name = "LST_CABANG", schema = "EKA")
public class ListCabang {

    @Id
    @Column(name = "LCA_ID")
    private String lcaId;

    @Column(name = "LCA_NAMA")
    private String nama;

    @Column(name = "BRANCH_NO")
    private String branchNo;

    @Column(name = "JALURDIS")
    private Integer jalurdis;

    @Column(name = "SUBJALUR")
    private String subjalur;

    @Column(name = "ID_COMP")
    private Double idComp;
    
}


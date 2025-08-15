package id.co.msiglife.pollux.schduler.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MST_CLIENT", schema = "EKA")
public class MasterClient {

    @Id
    @Column(name = "MCL_ID")
    private String mclId;

    @Column(name = "LTI_ID")
    private Integer titleId;

    @Column(name = "MCL_JENIS")
    private Integer jenis;

    @Column(name = "MCL_FIRST")
    private String first;

    @Column(name = "MCL_MIDDLE")
    private String middle;

    @Column(name = "MCL_LAST")
    private String last;

    @Column(name = "MCL_BLACKLIST")
    private Integer blacklist;

    @Column(name = "MCL_TGL_INPUT")
    private Date tglInput;

    @Column(name = "LUS_ID")
    private Integer lusId;

    @Column(name = "MCL_DATA_SALAH")
    private Integer dataSalah;
}

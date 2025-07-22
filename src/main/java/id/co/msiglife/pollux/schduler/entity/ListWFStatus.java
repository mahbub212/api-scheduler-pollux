package id.co.msiglife.pollux.schduler.entity;

import id.co.msiglife.pollux.schduler.composite.CompListWFStatus;
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
@IdClass(CompListWFStatus.class)
@Table(name = "LST_WF_STATUS", schema = "EKA")
public class ListWFStatus {

    @Id
    @Column(name = "LSWF_TYPE_ID")
    private Integer typeId;

    @Id
    @Column(name = "LSWF_STATUS_ID")
    private Integer statusId;

    @Column(name = "LSWF_STATUS_NAME")
    private String statusName;
}

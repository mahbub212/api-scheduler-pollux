package id.co.msiglife.pollux.schduler.entity;

import id.co.msiglife.pollux.schduler.composite.CompListWFConfig;
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
@IdClass(CompListWFConfig.class)
@Table(name = "LST_WF_CONFIG", schema = "EKA")
public class ListWFConfig {

    @Id
    @Column(name = "LSWF_TYPE_ID")
    private Integer typeId;

    @Id
    @Column(name = "LSWF_CONFIG_ID")
    private Integer configId;

    @Id
    @Column(name = "LSWF_STATE_ID_FROM")
    private Integer stateIdFrom;

    @Column(name = "LSWF_CONFIG_ACTION")
    private String configAction;

    @Column(name = "LSWF_ROLE_ID")
    private Integer roleId;

    @Column(name = "LSWF_STATE_ID_TO")
    private Integer stateIdTo;

    @Column(name = "LSWF_ACTION_ID_BEFORE")
    private Integer actionIdBefore;

    @Column(name = "LSWF_ACTION_ID_AFTER")
    private Integer actionIdAfter;

    @Column(name = "LSWF_LOGIC_ID")
    private Integer logicId;

    @Column(name = "MSWF_DOC_STATUS_KEY")
    private String mswfDocStatusKey;

    @Column(name = "MSWF_DOC_STATUS_VALUE")
    private Integer mswfDocStatusValue;

    @Column(name = "LSWF_BLOCK_CONFIG_SUB_LOGIC")
    private String blockConfigSubLogic;

    @Column(name = "LSWF_POSITION_ID")
    private Integer positionId;

    @Column(name = "LSWF_STATUS_ID")
    private Integer statusId;

    @Column(name = "LSWF_CONFIG_ROUTING_TYPE")
    private Integer configRoutingType;

    @Column(name = "LSWF_CONFIG_IS_FURTHER")
    private Integer configIsFurther;

    @Column(name = "IS_ACTIVE")
    private Integer isActive;
}

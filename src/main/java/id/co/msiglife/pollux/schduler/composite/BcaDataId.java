package id.co.msiglife.pollux.schduler.composite;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BcaDataId implements Serializable {
    private Integer tipeTrans;
    private String referenceNo;
    private String orderId;
    private String regSpaj;
    private Integer noUrut;
}

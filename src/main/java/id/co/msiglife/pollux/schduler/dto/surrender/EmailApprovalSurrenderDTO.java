package id.co.msiglife.pollux.schduler.dto.surrender;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailApprovalSurrenderDTO {
    private String regSpaj;
    private String noPolis;
    private Integer flowTypeId;
    private String patchAttach;
    private String meId;
    private String insuredName;
    private int isPolluxRate;
    private BigDecimal premi;
    private BigDecimal extraPremi;
    private BigDecimal nilaiNt;
    private int lastApprove;
}

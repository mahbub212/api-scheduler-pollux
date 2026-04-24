package id.co.msiglife.pollux.schduler.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BcaRiplayDTO {
        private String regSpaj;
        private String policyNo;
        private Date inputDate;
        private Date generateRiplayDate;
        private String namaTertanggung;
        private Date dob;
        private String lswfConfigAction;
        private String emailTertanggung;
        private String namaCabang;
        private String pathFile;
}

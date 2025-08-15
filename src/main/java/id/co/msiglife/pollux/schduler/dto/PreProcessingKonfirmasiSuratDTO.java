package id.co.msiglife.pollux.schduler.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreProcessingKonfirmasiSuratDTO {
    private String regSpaj;
    private Integer reminderDays;
}

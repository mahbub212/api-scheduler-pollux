package id.co.msiglife.pollux.schduler.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
public class SuratAutoCancelDTO {

    private String nomorSertifikat;
    private String namaBankCabang;
    private String namaTertanggung;
    private Date tanggalLahirTertanggung;
    private Integer usiaTertanggung;
    private Integer masaAsuransiTahun;
    private Integer masaAsuransiBulan;
    private BigDecimal premi;
    private Date tanggalCetakSertifikat;
    private String namaBankPks;
    private String namaRekeningPks;
    private String nomorRekeningPks;
    private String emailCabang;
    private String emailAo;
    private String regSpaj;
    private BigDecimal uangPertanggungan;

}

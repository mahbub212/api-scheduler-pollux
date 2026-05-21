package id.co.msiglife.pollux.schduler.service;

import id.co.msiglife.pollux.schduler.dto.SuratAutoCancelDTO;
import id.co.msiglife.pollux.schduler.util.FileProcessUtil;
import id.co.msiglife.pollux.schduler.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class SuratKonfirmasiPembatalanTest {

    @InjectMocks
    private SuratKonfirmasiPembatalanSertifikatService suratKonfirmasiPembatalanSertifikatService;

    @Mock
    private JwtUtil jwtUtil;

    // Real util
    private final FileProcessUtil fileProcessUtil = new FileProcessUtil();


    @BeforeEach
    void setup() throws Exception {
        inject(suratKonfirmasiPembatalanSertifikatService, "STORAGE_PATH", "target/test-document-output/");

        // inject FileProcessUtil
        inject(suratKonfirmasiPembatalanSertifikatService, "fileProcessUtil", fileProcessUtil);

        // SYARIAH_SIGNER_NAME
        inject(suratKonfirmasiPembatalanSertifikatService, "SYARIAH_SIGNER_NAME", "Renova Siregar");

        // SYARIAH_SIGNER_POSITION
        inject(suratKonfirmasiPembatalanSertifikatService, "SYARIAH_SIGNER_POSITION", "President Director");
    }

    @Test
    void generatePdfSuratPembatalanSyariahTest() throws Exception {
        SuratAutoCancelDTO suratAutoCancelDTO = buildAutoCancelDTO("PembatalanSyariah", true);

        List<SuratAutoCancelDTO> suratAutoCancelDTOList = new ArrayList<>();
        suratAutoCancelDTOList.add(suratAutoCancelDTO);

        // Call method directly (no need reflection actually)
        suratKonfirmasiPembatalanSertifikatService.generatePdfSuratPembatalanSertifikat(suratAutoCancelDTOList);
    }

    // =========================
    // HELPER METHODS
    // =========================

    private SuratAutoCancelDTO buildAutoCancelDTO(String regSpaj, boolean isSyariah) throws Exception {
        SuratAutoCancelDTO dto = new SuratAutoCancelDTO();

        dto.setRegSpaj(regSpaj);
        dto.setNomorSertifikat("nomorSertifikat");
        dto.setNamaBankCabang("Bank ABC Cabang Jakarta");
        dto.setNamaTertanggung("Budi Santoso");
        dto.setTanggalLahirTertanggung(new SimpleDateFormat("yyyy-MM-dd").parse("1990-01-15"));
        dto.setUsiaTertanggung(34);
        dto.setMasaAsuransiTahun(5);
        dto.setMasaAsuransiBulan(60);
        dto.setPremi(new BigDecimal("1500000"));
        dto.setTanggalCetakSertifikat(new Date());
        dto.setNamaBankPks("Bank ABC");
        dto.setNamaRekeningPks("PT Asuransi Sejahtera");
        dto.setNomorRekeningPks("1234567890");
        dto.setEmailCabang("cabang@bankabc.co.id");
        dto.setEmailAo("ao@bankabc.co.id");
        dto.setUangPertanggungan(new BigDecimal("100000000"));
        dto.setEmailTertanggung("budi.santoso@email.com");

        dto.setSyariah(isSyariah);

        return dto;
    }

    private static void inject(Object target, String field, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(field);
        f.setAccessible(true);
        f.set(target, value);
    }
}
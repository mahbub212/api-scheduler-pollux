package id.co.msiglife.pollux.schduler.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import id.co.msiglife.pollux.schduler.dto.ApiResponseDTO;
import id.co.msiglife.pollux.schduler.dto.SuratKonfirmasiPembatalanSertifikatDTO;
import id.co.msiglife.pollux.schduler.repository.MasterEmailRepository;
import id.co.msiglife.pollux.schduler.repository.MasterPolicyRepository;
import id.co.msiglife.pollux.schduler.util.FileProcessUtil;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static id.co.msiglife.pollux.schduler.util.ConvertUtil.convertDateToJakartaTime;
import static id.co.msiglife.pollux.schduler.util.ConvertUtil.currencyFormatID;

@Service
public class SuratKonfirmasiPembatalanSertifikatService {

    @Autowired
    MasterEmailRepository masterEmailRepository;

    @Autowired
    private FileProcessUtil fileProcessUtil;

    @Autowired
    MasterPolicyRepository masterPolicyRepository;

    @Value("${spring.custom.mailpool-attachment.path}")
    private String ATTACHMENT_PATH;

    @Value("${spring.custom.storage.path}")
    private String STORAGE_PATH;

    @PersistenceContext
    private EntityManager entityManager;

    public ApiResponseDTO<Map<String, Object>> sendSuratKonfirmasiPembatalanSertifikatEmail() throws Exception {
        Map<String, Object> result = new HashMap<>();

        List<Object[]> results = masterPolicyRepository.findAutoCancelByDays(21);

        List<SuratKonfirmasiPembatalanSertifikatDTO> dtoList = results.stream().map(row -> new SuratKonfirmasiPembatalanSertifikatDTO(
                (String) row[0],
                (String) row[1],
                (String) row[2],
                (Date) row[3],
                ((BigDecimal) row[4]).intValue(),
                ((BigDecimal) row[5]).intValue(),
                ((BigDecimal) row[6]).intValue(),
                (BigDecimal) row[7],
                (Date) row[8],
                (String) row[9],
                (String) row[10],
                (String) row[11],
                (String) row[12],
                (String) row[13],
                (String) row[14],
                (BigDecimal) row[15]
        )).collect(Collectors.toList());

        // read template file
        String today = convertDateToJakartaTime(LocalDate.now());
        String filePath = "template/surat_konfirmasi_pembatalan_sertifikat.html";
        String content;

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath)) {
            assert inputStream != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                StringBuilder contentBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    contentBuilder.append(line).append(System.lineSeparator());
                }
                content = contentBuilder.toString();
            }
        }

        // replace content resources
        String base64Font = this.fileProcessUtil.encodeFontToBase64("template/fonts/Ubuntu/Ubuntu-Regular.ttf");
        content = content.replace("[[MY_FONT_PATH]]", base64Font);

        String imgPathWTHeader = this.fileProcessUtil.getBase64Image("template/images/watermark_header.png");
        content = content.replace("../../images/watermark_header.png", imgPathWTHeader);

        String imgPathWt = this.fileProcessUtil.getBase64Image("template/images/watermark.png");
        content = content.replace("../../images/watermark.png", imgPathWt);

        String imgPathFileFooter1 = this.fileProcessUtil.getBase64Image("template/images/watermark_footer1.png");
        content = content.replace("../../images/watermark_footer1.png", imgPathFileFooter1);

        String imgPathFileFooter2 = this.fileProcessUtil.getBase64Image("template/images/watermark_footer2.png");
        content = content.replace("../../images/watermark_footer2.png", imgPathFileFooter2);

        for (SuratKonfirmasiPembatalanSertifikatDTO dto : dtoList) {
            // Fetch the email ID from the SQL function using a native SQL query
            String generatedId = entityManager.createNativeQuery("SELECT EKA.PAC_COUNTER.GET_EMAIL_ID FROM DUAL").getSingleResult().toString();
            Date sysDate = (Date) entityManager.createNativeQuery("SELECT SYSDATE FROM DUAL").getSingleResult();
            System.out.println("Printed generated email ID: " + generatedId);

            String editedContent = content; // reset content to template state

            // edit dynamic contents
            editedContent = editedContent.replace("{nomor_sertifikat}", dto.getNomorSertfikat());
            editedContent = editedContent.replace("{tanggal_hari_ini}", today);
            editedContent = editedContent.replace("{nama_bank_cabang}", dto.getNamaBankCabang());
            editedContent = editedContent.replace("{nama_tertanggung}", dto.getNamaTertanggung());
            editedContent = editedContent.replace("{tanggal_lahir_tertanggung}", convertDateToJakartaTime(dto.getTanggalLahirTertanggung()));
            editedContent = editedContent.replace("{usia_tertanggung}", dto.getUsiaTertanggung().toString());
            editedContent = editedContent.replace("{tenor}", dto.getMasaAsuransiTahun() + " Tahun" + (dto.getMasaAsuransiBulan() != null && dto.getMasaAsuransiBulan() > 0 ? dto.getMasaAsuransiBulan() + " Bulan" : ""));
            editedContent = editedContent.replace("{premi_asuransi}", currencyFormatID(dto.getPremi()));
            editedContent = editedContent.replace("{tanggal_cetak_sertifikat}", convertDateToJakartaTime(dto.getTanggalCetakSertifikat()));
            editedContent = editedContent.replace("{nama_bank_pks}", dto.getNamaBankPks());
            editedContent = editedContent.replace("{atas_nama_pks}", dto.getNamaRekeningPks());
            editedContent = editedContent.replace("{rekening_pks}", dto.getNomorRekeningPks());
            editedContent = editedContent.replace("{up}", currencyFormatID(dto.getUangPertanggungan()));
            editedContent = editedContent.replace("{tanggal_terakhir_pembayaran_premi}", convertDateToJakartaTime(LocalDate.now().plusDays(30)));

            // create string HTML to a JSoup Document to render XHTML
            Document document = Jsoup.parse(content);
            document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);

            // create the folder if it doesn't exist
            String folderPath = STORAGE_PATH + dto.getRegSpaj();

            try {
                File folder = new File(folderPath);
                File parentDirectory = folder.getParentFile();
                if (parentDirectory.canWrite()) {
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                } else {
                    throw new IOException("No write permissions in the parent directory: " + parentDirectory);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // PdfRendererBuilder will take this XHTML document and create a PDF as the output file
            String outputPdf = folderPath + "/" + dto.getRegSpaj() + "_SURAT_KONFIRMASI_PEMBATALAN_SERTIFIKAT.pdf";

            try (OutputStream os = Files.newOutputStream(Paths.get(outputPdf))) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.withUri(outputPdf);
                builder.toStream(os);
                builder.withW3cDocument(new W3CDom().fromJsoup(document), folderPath + "/");
                builder.run();
            }

            // save into MasterUploadDokumen
            this.fileProcessUtil.createOrUpdateFileMasterUploadDokumen(dto.getRegSpaj(), 18, outputPdf, 0);
        }

        return new ApiResponseDTO<>(200, "Email sent successfully.", result);
    }
}

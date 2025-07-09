package id.co.msiglife.pollux.schduler.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import id.co.msiglife.pollux.schduler.dto.ApiResponseDTO;
import id.co.msiglife.pollux.schduler.dto.SuratAutoCancelDTO;
import id.co.msiglife.pollux.schduler.entity.MasterEmail;
import id.co.msiglife.pollux.schduler.repository.MasterEmailRepository;
import id.co.msiglife.pollux.schduler.repository.MasterPolicyRepository;
import id.co.msiglife.pollux.schduler.util.FileProcessUtil;
import id.co.msiglife.pollux.schduler.util.StaticValue;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static id.co.msiglife.pollux.schduler.util.ConvertUtil.*;

@Service
public class SuratKonfirmasiAwalService {

    @Value("${spring.custom.storage.path}")
    private String STORAGE_PATH;

    @Value("${spring.custom.mailpool-attachment.path}")
    private String ATTACHMENT_PATH;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    MasterEmailRepository masterEmailRepository;

    @Autowired
    MasterPolicyRepository masterPolicyRepository;

    @Autowired
    private FileProcessUtil fileProcessUtil;

    // running every 01:00 AM, every day
    @Scheduled(cron = "0 0 1 * * ?")
    public ApiResponseDTO<Map<String, Object>> sendSuratKonfirmasiEmail() {
        Map<String, Object> result = new HashMap<>();

        List<Object[]> results = masterPolicyRepository.findAutoCancelByDays(StaticValue.SURAT_KONFIRMASI_AWAL_REMINDER_DAYS);

        List<SuratAutoCancelDTO> dtoList = results.stream().map(row -> new SuratAutoCancelDTO(
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
                (BigDecimal) row[15],
                (String) row[16]
        )).collect(Collectors.toList());

        // read template file
        String today = convertDateToJakartaTime(LocalDate.now());
        String filePath = "template/surat_konfirmasi_awal_template.html";
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
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // replace content resources
        try {
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        List<MasterEmail> emailList = new ArrayList<>();

        for (SuratAutoCancelDTO dto : dtoList) {

            // Fetch the email ID from the SQL function using a native SQL query
            String generatedId = entityManager.createNativeQuery("SELECT EKA.PAC_COUNTER.GET_EMAIL_ID FROM DUAL").getSingleResult().toString();
            Date sysDate = (Date) entityManager.createNativeQuery("SELECT SYSDATE FROM DUAL").getSingleResult();

            String editedContent = content; // reset content to template state

            // edit dynamic contents
            editedContent = editedContent.replace("{nomor_sertifikat}", dto.getNomorSertifikat());
            editedContent = editedContent.replace("{tanggal_hari_ini}", today);
            editedContent = editedContent.replace("{nama_bank_cabang}", dto.getNamaBankCabang());
            editedContent = editedContent.replace("{nama_tertanggung}", dto.getNamaTertanggung());
            editedContent = editedContent.replace("{tanggal_lahir_tertanggung}", convertDateToJakartaTime(dto.getTanggalLahirTertanggung()));
            editedContent = editedContent.replace("{usia_tertanggung}", dto.getUsiaTertanggung().toString());
            editedContent = editedContent.replace("{uang_pertanggungan}", currencyFormatID(dto.getUangPertanggungan()));
            editedContent = editedContent.replace("{tenor}", dto.getMasaAsuransiTahun() + " Tahun " + (dto.getMasaAsuransiBulan() != null && dto.getMasaAsuransiBulan() > 0 ? dto.getMasaAsuransiBulan() + " Bulan" : ""));
            editedContent = editedContent.replace("{premi_asuransi}", currencyFormatID(dto.getPremi()));
            editedContent = editedContent.replace("{tanggal_cetak_sertifikat}", convertDateToJakartaTime(dto.getTanggalCetakSertifikat()));
            editedContent = editedContent.replace("{nama_bank_pks}", dto.getNamaBankPks());
            editedContent = editedContent.replace("{atas_nama_pks}", dto.getNamaRekeningPks());
            editedContent = editedContent.replace("{rekening_pks}", dto.getNomorRekeningPks());

            // create string HTML to a JSoup Document to render XHTML
            Document document = Jsoup.parse(editedContent);
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
            String outputPdf = folderPath + "/" + dto.getRegSpaj() + "_SURAT_KONFIRMASI_AWAL.pdf";

            try (OutputStream os = Files.newOutputStream(Paths.get(outputPdf))) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.withUri(outputPdf);
                builder.toStream(os);
                builder.withW3cDocument(new W3CDom().fromJsoup(document), folderPath + "/");
                builder.run();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // save into MasterUploadDokumen
            this.fileProcessUtil.createOrUpdateFileMasterUploadDokumen(dto.getRegSpaj(), 16, outputPdf, 0);

            // Copy file from file storage into attachment directory
            // Define the folder path for attachments
            // (ex: //storage.sinarmasmsiglife.co.id/mailpools/attachments/2023/09/20230923661730)
            String attachmentFolderPath = ATTACHMENT_PATH + generatedId.substring(0, 4) + "/" + generatedId.substring(4, 6) + "/" + generatedId;

            // create the folder if it doesn't exist
            try {
                File attachmentFolder = new File(attachmentFolderPath);
                File parentDirectory = attachmentFolder.getParentFile();
                if (parentDirectory.canWrite()) {
                    if (!attachmentFolder.exists()) {
                        attachmentFolder.mkdirs();
                    }
                } else {
                    throw new IOException("No write permissions in the parent directory: " + parentDirectory);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // source file path
            String sourceFilePath = folderPath + "/" + dto.getRegSpaj() + "_SURAT_KONFIRMASI_AWAL.pdf";
            Path sourcePath = Paths.get(sourceFilePath).toAbsolutePath();

            // validate source of both file
            if (!Files.exists(sourcePath) || !Files.isReadable(sourcePath)) {
                try {
                    throw new IOException("KPS source file does not exist or is not readable: " + sourceFilePath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            // destination file path
            String destinationFilePath = attachmentFolderPath + "/" + dto.getRegSpaj() + "_SURAT_KONFIRMASI_AWAL.pdf";
            Path destinationPath = Paths.get(destinationFilePath).toAbsolutePath();

            // copy the file
            try {
                Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // check if the copies of both files are readable
            if (!Files.isReadable(destinationPath)) {
                try {
                    throw new IOException("The copied KPS file is not readable: " + destinationFilePath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            // set emailTo, subject, message, etc.
            String emailTo = stringBlank(dto.getEmailAo()) ? "" : dto.getEmailAo() + ";" + (stringBlank(dto.getEmailCabang()) ? "" : dto.getEmailCabang());

            String subject = "Surat Konfirmasi Pembayaran Premi a/n " + dto.getNamaTertanggung() + " No Sertifikat " + dto.getNomorSertifikat();
            String message = templateSuratKonfirmasiEmail(dto.getNamaBankCabang(), dto.getNamaTertanggung(), dto.getNomorSertifikat(), convertDateToJakartaTime(dto.getTanggalCetakSertifikat()));

            MasterEmail emailEntity = new MasterEmail();
            emailEntity.setId(generatedId);
            emailEntity.setSystem("POLLUX");
            emailEntity.setShowSystem(0);
            emailEntity.setShowFooter(0);
            emailEntity.setFrom("cosmos@msiglife.co.id");
            emailEntity.setFromAlias("Notifikasi POLLUX");
            emailEntity.setTo(emailTo);
            emailEntity.setCc("underwritingmriproses@msiglife.co.id");
            emailEntity.setBcc(null);
            emailEntity.setSubject(subject);
            emailEntity.setMessage(message);
            emailEntity.setMecId(0);
            emailEntity.setHtml(1);
            emailEntity.setStatus(0);
            emailEntity.setStatusMessage(null);
            emailEntity.setCount(0);
            emailEntity.setUserId(0);  // Set the appropriate user ID
            emailEntity.setInputDate(sysDate);
            emailEntity.setSentDate(null); // Leave it as null or set to a specific date if scheduling
            emailEntity.setSchedulerDate(sysDate); // Set the specific date for scheduling or leave it as sysdate for immediate sending
            emailEntity.setPriority(50);
            emailEntity.setJenisId(50);
            emailEntity.setFlagNew(0);
            emailEntity.setRegSpaj(dto.getRegSpaj());
            emailEntity.setPathAttach(destinationPath.toString());

            emailList.add(emailEntity);
            System.out.println("KPS and Riplay Email Sent Successfully...");
        }

        masterEmailRepository.saveAll(emailList);

        result.put("dto", dtoList);
        return new ApiResponseDTO<>(200, "Email sent successfully.", result);

    }

    private String templateSuratKonfirmasiEmail(String namaCabang, String namaTertanggung, String noSertifikat, String tanggalTerbitSertifikat) {
        return "<!DOCTYPE html>"
                + "<html lang=\"id\">"
                + "<head>"
                + "<meta charset=\"UTF-8\">"
                + "<title>Surat Konfirmasi Pembayaran Premi</title>"
                + "</head>"
                + "<body style=\"font-family: Arial, sans-serif; font-size: 14px; color: #000000;\">"
                + "<p style=\"font-family: Arial, sans-serif; font-size: 14px; line-height: 1.5;\">Kepada Yth.<br><strong>" + namaCabang + "</strong></p><br>"
                + "<p style=\"font-family: Arial, sans-serif; font-size: 14px; line-height: 1.5;\">Kami mengucapkan terima kasih kepada Bapak/Ibu yang telah mempercayakan kami, PT MSIG Life Insurance Indonesia Tbk. untuk memberikan perlindungan Asuransi Jiwa Kredit kepada Bapak/Ibu.</p>"
                + "<p style=\"font-family: Arial, sans-serif; font-size: 14px; line-height: 1.5;\">Berikut kami sampaikan <strong>Surat Konfirmasi Pembayaran Premi</strong> untuk sertifikat Asuransi Jiwa Kredit atas nama " + namaTertanggung + " dengan nomor sertifikat " + noSertifikat + " yang telah diterbitkan pada tanggal " + tanggalTerbitSertifikat + ".</p>"
                + "<p style=\"font-family: Arial, sans-serif; font-size: 14px; line-height: 1.5;\">Sampai dengan hari ini, untuk pembayaran premi atas sertifikat tersebut belum kami terima.<br>"
                + "Sehubungan dengan hal tersebut, kami mohon agar pembayaran premi dapat segera diproses guna menjamin keberlangsungan manfaat perlindungan asuransi bagi tertanggung.</p>"
                + "<p style=\"font-family: Arial, sans-serif; font-size: 14px; line-height: 1.5;\">Informasi lebih lanjut terkait detail pembayaran terlampir pada surat yang kami kirimkan bersama email ini.</p>"
                + "<p style=\"font-family: Arial, sans-serif; font-size: 14px; line-height: 1.5;\">Apabila premi telah dibayarkan, mohon kesediaannya untuk mengirimkan bukti pembayaran ke email <a href=\"mailto:collectionmri@msiglife.co.id\" style=\"color: #0066cc;\">collectionmri@msiglife.co.id</a></p>"
                + "<p style=\"font-family: Arial, sans-serif; font-size: 14px; line-height: 1.5;\">Demikian kami sampaikan. Atas perhatian dan kerja sama Bapak/Ibu, kami ucapkan terima kasih.</p><br>"
                + "<p style=\"font-family: Arial, sans-serif; font-size: 14px; line-height: 1.5;\"><strong>Credit Life Billing and Collection</strong></p>"
                + "</body>"
                + "</html>";
    }

}

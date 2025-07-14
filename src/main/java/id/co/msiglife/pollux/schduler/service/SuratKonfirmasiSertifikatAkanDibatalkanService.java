package id.co.msiglife.pollux.schduler.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import id.co.msiglife.pollux.schduler.dto.ApiResponseDTO;
import id.co.msiglife.pollux.schduler.dto.SuratAutoCancelDTO;
import id.co.msiglife.pollux.schduler.entity.MasterAutoCancelEmailLog;
import id.co.msiglife.pollux.schduler.entity.MasterEmail;
import id.co.msiglife.pollux.schduler.repository.MasterEmailRepository;
import id.co.msiglife.pollux.schduler.repository.MasterPolicyRepository;
import id.co.msiglife.pollux.schduler.repository.MasterAutoCancelEmailLogRepository;
import id.co.msiglife.pollux.schduler.util.ConvertUtil;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static id.co.msiglife.pollux.schduler.util.ConvertUtil.*;
import static id.co.msiglife.pollux.schduler.util.StaticValue.SERTIFIKAT_AKAN_DIBATALKAN_REMINDER_DAYS;

@Service
public class SuratKonfirmasiSertifikatAkanDibatalkanService {

    @Autowired
    MasterEmailRepository masterEmailRepository;

    @Autowired
    private FileProcessUtil fileProcessUtil;

    @Autowired
    MasterPolicyRepository masterPolicyRepository;

    @Autowired
    MasterAutoCancelEmailLogRepository masterAutoCancelEmailLogRepository;

    @Value("${spring.custom.mailpool-attachment.path}")
    private String ATTACHMENT_PATH;

    @Value("${spring.custom.storage.path}")
    private String STORAGE_PATH;

    @PersistenceContext
    private EntityManager entityManager;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void sendKonfirmasiSertifikatAkanDibatalkanEmail() throws Exception {
        System.out.println("Running Scheduler for Surat Konfirmasi Sertifikat akan Dibatalkan at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", new Locale("id", "ID"))));
        List<SuratAutoCancelDTO> sertifikatAkanDibatalkanDTOList = getTunggakanPremi(StaticValue.SERTIFIKAT_AKAN_DIBATALKAN_REMINDER_DAYS);
        System.out.println("=================== START GENERATE SURAT KONFIRMASI SERTIFIKAT AKAN DIBATALKAN PDF (" + sertifikatAkanDibatalkanDTOList.size() + ") Data ===================");
        generatePdfSuratKonfirmasiSertifikatAkanDibatalkan(sertifikatAkanDibatalkanDTOList);
        System.out.println("=================== END GENERATED SURAT KONFIRMASI SERTIFIKAT AKAN DIBATALKAN PDF ===================");
        System.out.println("=================== START SENDING EMAIL SURAT KONFIRMASI SERTIFIKAT AKAN DIBATALKAN PDF ===================");
        generateAutoMail(sertifikatAkanDibatalkanDTOList);

        System.out.println("=================== END SEND EMAIL SURAT KONFIRMASI SERTIFIKAT AKAN DIBATALKAN PDF ===================");
    }

//    @Scheduled(cron = "0 0/15 * * * ?")
    public void retrySendingKonfirmasiSertifikatAkanDibatalkanEmail() throws Exception {
        System.out.println("Retrying Scheduler for Surat Konfirmasi Sertifikat akan Dibatalkan at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", new Locale("id", "ID"))));
        List<SuratAutoCancelDTO> sertifikatAkanDibatalkanDTOList = getTunggakanPremi(StaticValue.SERTIFIKAT_AKAN_DIBATALKAN_REMINDER_DAYS);
        System.out.println("=================== RETRYING START GENERATE SURAT KONFIRMASI SERTIFIKAT AKAN DIBATALKAN PDF " + sertifikatAkanDibatalkanDTOList.size() + " Data ===================");
        generatePdfSuratKonfirmasiSertifikatAkanDibatalkan(sertifikatAkanDibatalkanDTOList);
        System.out.println("=================== RETRYING END GENERATED SURAT KONFIRMASI SERTIFIKAT AKAN DIBATALKAN PDF ===================");
        System.out.println("=================== RETRYING START SENDING EMAIL SURAT KONFIRMASI SERTIFIKAT AKAN DIBATALKAN PDF ===================");
        generateAutoMail(sertifikatAkanDibatalkanDTOList);
        System.out.println("=================== RETRYING END SEND EMAIL SURAT KONFIRMASI SERTIFIKAT AKAN DIBATALKAN PDF ===================");
    }

    private void generateAutoMail(List<SuratAutoCancelDTO> sertifikatAkanDibatalkanDTOList) {
        for(SuratAutoCancelDTO dto : sertifikatAkanDibatalkanDTOList){
            try {
                Date sysDate = (Date) entityManager.createNativeQuery("SELECT SYSDATE FROM DUAL").getSingleResult();

                String sourceFilePath = STORAGE_PATH + "/" + dto.getRegSpaj()  + "/" + dto.getRegSpaj() + "_SURAT_KONFIRMASI_SERTIFIKAT_AKAN_DIBATALKAN.pdf";
                Path sourcePath = Paths.get(sourceFilePath).toAbsolutePath();

                String generatedId = entityManager.createNativeQuery("SELECT EKA.PAC_COUNTER.GET_EMAIL_ID FROM DUAL").getSingleResult().toString();
                String attachmentFolderPath = ATTACHMENT_PATH + generatedId.substring(0, 4) + "/" + generatedId.substring(4, 6) + "/" + generatedId;

                File folder = new File(attachmentFolderPath);
                if (!folder.exists()) {
                    if (!folder.mkdirs()) {
                        throw new IOException("Failed to create directory: " + attachmentFolderPath);
                    }
                }

                // Validasi keberadaan file
                if (Files.exists(sourcePath) && Files.isReadable(sourcePath)) {
                    try {
                        // destination file path
                        String destinationFilePath = attachmentFolderPath + "/" + dto.getRegSpaj() + "_SURAT_KONFIRMASI_SERTIFIKAT_AKAN_DIBATALKAN.pdf";
                        Path destinationPath = Paths.get(destinationFilePath).toAbsolutePath();
                        Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                        if (!Files.isReadable(destinationPath)) {
                            try {
                                throw new IOException("The copied Surat Konfirmasi Sertifikat akan Dibatalkan file is not readable: " + destinationFilePath);
                            } catch (IOException e) {
                                System.err.println("Failed to copy PDF for regSpaj: " + dto.getRegSpaj() + " | Error: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }

                        //set emailTo, subject, message, etc.
                        String emailTo = stringBlank(dto.getEmailAo()) ? "" : dto.getEmailAo() + ";" + (stringBlank(dto.getEmailCabang()) ? "" : dto.getEmailCabang());

                        String subject = "Surat Konfirmasi Sertifikat Akan Dibatalkan a/n " + dto.getNamaTertanggung() + " No Sertifikat " + dto.getNomorSertifikat();
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
                        emailEntity.setPathAttach(destinationFilePath);
                        masterEmailRepository.save(emailEntity);

                        String savedId = emailEntity.getId();
                        MasterAutoCancelEmailLog masterAutoCancelEmailLog = new MasterAutoCancelEmailLog();
                        masterAutoCancelEmailLog.setMeId(savedId);
                        masterAutoCancelEmailLog.setRegSpaj(dto.getRegSpaj());
                        masterAutoCancelEmailLog.setPathAttach(destinationPath.toString());
                        masterAutoCancelEmailLog.setCreatedAt(sysDate);
                        masterAutoCancelEmailLog.setReminderDays(SERTIFIKAT_AKAN_DIBATALKAN_REMINDER_DAYS);

                        masterAutoCancelEmailLogRepository.save(masterAutoCancelEmailLog);
                    } catch (IOException e) {
                        System.err.println("Failed to generate PDF for regSpaj: " + dto.getRegSpaj() + " | Error: " + e.getMessage());
                        e.printStackTrace();
                    }
                }else{
                    throw new RuntimeException("File Attachment not exist");
                }
            }catch (Exception e){
                System.err.println("Error when getting file attachment");
                e.printStackTrace();
            }
        }
    }

    private void generatePdfSuratKonfirmasiSertifikatAkanDibatalkan(List<SuratAutoCancelDTO> suratAutoCancelDtoList) {
        try {
            // read template file
            String today = convertDateToJakartaTime(LocalDate.now());
            String filePath = "template/surat_konfirmasi_sertifikat_akan_dibatalkan_template.html";
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

            for(SuratAutoCancelDTO dto : suratAutoCancelDtoList){
                try{
                    String editedContent = content; // reset content to template state

                    // create the folder if it doesn't exist
                    String folderPath = STORAGE_PATH + dto.getRegSpaj();
                    String outputPdf = folderPath + "/" + dto.getRegSpaj() + "_SURAT_KONFIRMASI_SERTIFIKAT_AKAN_DIBATALKAN.pdf";
                    Path sourcePath = Paths.get(outputPdf).toAbsolutePath();
                    System.out.println("Checking file existence at path: " + outputPdf);
                    System.out.println("Absolute path: " + Paths.get(outputPdf).toAbsolutePath());
                    System.out.println("Exists? " + Files.exists(Paths.get(outputPdf)));
                    System.out.println("Exists path? " + Files.exists(sourcePath));

                    if (!Files.exists(sourcePath)) {
                        // edit dynamic contents
                        editedContent = editedContent.replace("{nomor_sertifikat}", dto.getNomorSertifikat());
                        editedContent = editedContent.replace("{tanggal_hari_ini}", today);
                        editedContent = editedContent.replace("{nama_bank_cabang}", dto.getNamaBankCabang());
                        editedContent = editedContent.replace("{nama_tertanggung}", dto.getNamaTertanggung());
                        editedContent = editedContent.replace("{tanggal_lahir_tertanggung}", convertDateToJakartaTime(dto.getTanggalLahirTertanggung()));
                        editedContent = editedContent.replace("{usia_tertanggung}", dto.getUsiaTertanggung().toString());
                        editedContent = editedContent.replace("{tenor}", dto.getMasaAsuransiTahun() + " Tahun " + (dto.getMasaAsuransiBulan() != null && dto.getMasaAsuransiBulan() > 0 ? dto.getMasaAsuransiBulan() + " Bulan" : ""));
                        editedContent = editedContent.replace("{premi_asuransi}", currencyFormatID(dto.getPremi()));
                        editedContent = editedContent.replace("{tanggal_cetak_sertifikat}", convertDateToJakartaTime(dto.getTanggalCetakSertifikat()));
                        editedContent = editedContent.replace("{nama_bank_pks}", dto.getNamaBankPks());
                        editedContent = editedContent.replace("{atas_nama_pks}", dto.getNamaRekeningPks());
                        editedContent = editedContent.replace("{rekening_pks}", dto.getNomorRekeningPks());
                        editedContent = editedContent.replace("{up}", currencyFormatID(dto.getUangPertanggungan()));
                        editedContent = editedContent.replace("{tanggal_terakhir_pembayaran_premi}", convertDateToJakartaTime(LocalDate.now().plusDays(30)));

                        // create string HTML to a JSoup Document to render XHTML
                        Document document = Jsoup.parse(editedContent);
                        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);

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
                            System.err.println("Failed to generate PDF for regSpaj: " + dto.getRegSpaj() + " | Error: " + e.getMessage());
                            e.printStackTrace();
                        }

                        try (OutputStream os = Files.newOutputStream(Paths.get(outputPdf))) {
                            PdfRendererBuilder builder = new PdfRendererBuilder();
                            builder.withUri(outputPdf);
                            builder.toStream(os);
                            builder.withW3cDocument(new W3CDom().fromJsoup(document), folderPath + "/");
                            builder.run();
                        }

                        // save into MasterUploadDokumen
                        this.fileProcessUtil.createOrUpdateFileMasterUploadDokumen(dto.getRegSpaj(), 15, outputPdf, 0);
                    }else{
                        System.out.println("Skipping PDF generation for Surat Konfirmasi Sertifikat akan Dibatalkan; file already exists for regSpaj: " + dto.getRegSpaj());
                    }
                } catch (Exception e) {
                    System.err.println("Failed to generate PDF for regSpaj: " + dto.getRegSpaj() + " | Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String templateSuratKonfirmasiEmail(String namaCabang, String namaTertanggung, String noSertifikat, String tanggalTerbitSertifikat) {
        return "<!DOCTYPE html>\n"
                + "<html lang=\"id\">\n"
                + "<head>\n"
                + "    <meta charset=\"UTF-8\">\n"
                + "    <title>Surat Konfirmasi Sertifikat Akan Dibatalkan</title>\n"
                + "</head>\n"
                + "<body>\n"
                + "\n"
                + "    <p>Kepada Yth.<br>\n"
                + "    <strong>" + namaCabang + "</strong></p>\n"
                + "\n"
                + "    <p>Kami mengucapkan terima kasih kepada Bapak/Ibu yang telah mempercayakan kami, PT MSIG Life Insurance Indonesia Tbk. untuk memberikan perlindungan Asuransi Jiwa Kredit kepada Bapak/Ibu.</p>\n"
                + "\n"
                + "    <p>Berikut kami sampaikan <strong>SURAT KONFIRMASI SERTIFIKAT AKAN DIBATALKAN</strong> untuk sertifikat Asuransi Jiwa Kredit atas nama " + namaTertanggung + " dengan nomor sertifikat " + noSertifikat + " yang telah diterbitkan pada tanggal " + tanggalTerbitSertifikat + ".</p>\n"
                + "\n"
                + "    <p>Sampai dengan hari ini, untuk pembayaran premi atas sertifikat tersebut belum kami terima.<br>\n"
                + "    Sehubungan dengan hal tersebut, kami mohon agar pembayaran premi dapat segera diproses guna menjamin keberlangsungan manfaat perlindungan asuransi bagi tertanggung.</p>\n"
                + "\n"
                + "    <p>Informasi lebih lanjut terkait detail pembayaran terlampir pada surat yang kami kirimkan bersama email ini.</p>\n"
                + "\n"
                + "    <p>Apabila premi telah dibayarkan, mohon kesediaannya untuk mengirimkan bukti pembayaran ke email <a href=\"mailto:collectionmri@msiglife.co.id\">collectionmri@msiglife.co.id</a></p>\n"
                + "\n"
                + "    <p>Demikian kami sampaikan. Atas perhatian dan kerja sama Bapak/Ibu, kami ucapkan terima kasih.</p>\n"
                + "\n"
                + "    <br><br>\n"
                + "    <p>Credit Life Billing and Collection</p>\n"
                + "\n"
                + "</body>\n"
                + "</html>";

    }

    public List<SuratAutoCancelDTO> getTunggakanPremi(Integer days) {
        List<Object[]> listData = masterPolicyRepository.findAutoCancelByDays(days);
        List<SuratAutoCancelDTO> result = new ArrayList<>();

        for (Object[] row : listData) {
            SuratAutoCancelDTO dto = new SuratAutoCancelDTO();
            dto.setNomorSertifikat(ConvertUtil.safeString(row[0]));
            dto.setNamaBankCabang(ConvertUtil.safeString(row[1]));
            dto.setNamaTertanggung(ConvertUtil.safeString(row[2]));
            dto.setTanggalLahirTertanggung(ConvertUtil.safeDate(row[3]));
            dto.setUsiaTertanggung(ConvertUtil.safeInt(row[4]));
            dto.setMasaAsuransiTahun(ConvertUtil.safeInt(row[5]));
            dto.setMasaAsuransiBulan(ConvertUtil.safeInt(row[6]));
            dto.setPremi(ConvertUtil.safeBigDecimal(row[7]));
            dto.setTanggalCetakSertifikat(ConvertUtil.safeDate(row[8]));
            dto.setNamaBankPks(ConvertUtil.safeString(row[9]));
            dto.setNamaRekeningPks(ConvertUtil.safeString(row[10]));
            dto.setNomorRekeningPks(ConvertUtil.safeString(row[11]));
            dto.setEmailCabang(ConvertUtil.safeString(row[12]));
            dto.setEmailAo(ConvertUtil.safeString(row[13]));
            dto.setRegSpaj(ConvertUtil.safeString(row[14]));
            dto.setUangPertanggungan(ConvertUtil.safeBigDecimal(row[15]));
            dto.setEmailTertanggung(ConvertUtil.safeString(row[16]));
            result.add(dto);
        }

        return result;
    }
}
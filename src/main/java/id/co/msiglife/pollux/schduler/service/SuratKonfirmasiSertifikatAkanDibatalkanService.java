package id.co.msiglife.pollux.schduler.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    public void generateAutoMail(List<SuratAutoCancelDTO> sertifikatAkanDibatalkanDTOList) {
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
                        emailEntity.setCc("underwritingmriproses@msiglife.co.id;collectionmri@msiglife.co.id");
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
                        MasterAutoCancelEmailLog masterAutoCancelEmailLog = masterAutoCancelEmailLogRepository.findByRegSpajAndReminderDays(dto.getRegSpaj(), StaticValue.SERTIFIKAT_AKAN_DIBATALKAN_REMINDER_DAYS);

                        if (masterAutoCancelEmailLog == null) {
                            masterAutoCancelEmailLog = new MasterAutoCancelEmailLog();
                        }

                        masterAutoCancelEmailLog.setMeId(savedId);
                        masterAutoCancelEmailLog.setRegSpaj(dto.getRegSpaj());
                        masterAutoCancelEmailLog.setPathAttach(destinationPath.toString());
                        masterAutoCancelEmailLog.setUpdatedAt(sysDate);
                        masterAutoCancelEmailLog.setFlagDone(1);
                        masterAutoCancelEmailLog.setFlagPolisCanceled(0);
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

    public void generatePdfSuratKonfirmasiSertifikatAkanDibatalkan(List<SuratAutoCancelDTO> suratAutoCancelDtoList) {
        try {
            // read template file
            String today = convertDateToJakartaTime(LocalDate.now());

            String headerKonven = "template/images/watermark_header.png";
            String footerKonven1 = "template/images/watermark_footer1.png";
            String footerKonven2 = "template/images/watermark_footer2.png";
            String headerSyariah = "template/images/watermark_header_syariah.png";
            String footerSyariah1 = "template/images/watermark_footer1_syariah_2.png";
            String footerSyariah2 = "template/images/watermark_footer1_syariah_3.png";

            String templateSyariah = initTemplateResource(loadTemplate(getTemplatePath(true)), headerSyariah, footerSyariah1, footerSyariah2);
            String templateKonven = initTemplateResource(loadTemplate(getTemplatePath(false)), headerKonven, footerKonven1, footerKonven2);

            for(SuratAutoCancelDTO dto : suratAutoCancelDtoList){
                if (dto.isSyariah()) {
                    processSuratSertifikatAkanDibatalkan(dto, templateSyariah, today);
                } else {
                    processSuratSertifikatAkanDibatalkan(dto, templateKonven, today);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void processSuratSertifikatAkanDibatalkan(SuratAutoCancelDTO dto, String content, String today) {

        try{
            String editedContent = content; // reset content to template state
            // create the folder if it doesn't exist
            String folderPath = STORAGE_PATH + dto.getRegSpaj();
            String outputPdf = folderPath + "/" + dto.getRegSpaj() + "_SURAT_KONFIRMASI_SERTIFIKAT_AKAN_DIBATALKAN.pdf";
            Path sourcePath = Paths.get(outputPdf).toAbsolutePath();
            LocalDate tanggalCetakSertifikat = dto.getTanggalCetakSertifikat() == null ? null : dto.getTanggalCetakSertifikat().toInstant().atZone(ZoneId.of("Asia/Jakarta")).toLocalDate();
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
                editedContent = editedContent.replace("{tanggal_terakhir_pembayaran_premi}", tanggalCetakSertifikat == null ? "" : convertDateToJakartaTime(tanggalCetakSertifikat.plusDays(30)));

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

    private String getTemplatePath(boolean isSyariah) {
        return isSyariah
                ? "template/syariahTemplate/surat_konfirmasi_sertifikat_akan_dibatalkan_syariah_template.html"
                : "template/surat_konfirmasi_sertifikat_akan_dibatalkan_template.html";
    }

    private String loadTemplate(String filePath) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath)) {
            if (inputStream == null) {
                throw new RuntimeException("Template not found: " + filePath);
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append(System.lineSeparator());
                }
                return sb.toString();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String initTemplateResource(String content, String header, String footer1, String footer2) {
        try {

            String base64Font = fileProcessUtil.encodeFontToBase64(
                    "template/fonts/Ubuntu/Ubuntu-Regular.ttf");
            content = content.replace("[[MY_FONT_PATH]]", base64Font);

            content = content.replace("../../images/watermark_header.png",
                    fileProcessUtil.getBase64Image(header));

            content = content.replace("../../images/watermark.png",
                    fileProcessUtil.getBase64Image("template/images/watermark.png"));

            content = content.replace("../../images/watermark_footer1.png",
                    fileProcessUtil.getBase64Image(footer1));

            content = content.replace("../../images/watermark_footer2.png",
                    fileProcessUtil.getBase64Image(footer2));

            return content;
        } catch (Exception e) {
            throw new RuntimeException("Failed init template resource", e);
        }
    }

    private String templateSuratKonfirmasiEmail(String namaCabang, String namaTertanggung, String noSertifikat, String tanggalTerbitSertifikat) {
        StringBuilder emailContent = new StringBuilder();

        emailContent.append("<!DOCTYPE html>");
        emailContent.append("<html lang=\"id\">");
        emailContent.append("<head>");
        emailContent.append("  <meta http-equiv=\"x-ua-compatible\" content=\"ie=edge\">");
        emailContent.append("  <meta charset=\"UTF-8\">");
        emailContent.append("  <meta name=\"x-apple-disable-message-reformatting\">");
        emailContent.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
        emailContent.append("  <title>Surat Konfirmasi Sertifikat Akan Dibatalkan</title>");
        emailContent.append("</head>");
        emailContent.append("<body style=\"font-family: Arial, sans-serif; font-size: 14px; color: #000000;\">");

        // Outer wrapper table (email-friendly)
        emailContent.append("  <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"background:#ffffff;\">");
        emailContent.append("    <tr>");
        emailContent.append("      <td align=\"left\">");

        // Content blocks
        emailContent.append("<div style=\"margin:0 0 12px 0;\">Kepada Yth.<br>")
                    .append("<strong>")
                    .append(namaCabang)
                    .append("</strong>")
                .append("</div>");

        emailContent.append("<div style=\"margin:0 0 12px 0;\">")
                    .append("Kami mengucapkan terima kasih kepada Bapak/Ibu yang telah mempercayakan kami, ")
                    .append("PT MSIG Life Insurance Indonesia Tbk. untuk memberikan perlindungan Asuransi Jiwa Kredit kepada Bapak/Ibu.")
                .append("</div>");

        emailContent.append("<div style=\"margin:0 0 12px 0;\">")
                    .append("Berikut kami sampaikan <strong>SURAT KONFIRMASI SERTIFIKAT AKAN DIBATALKAN</strong> ")
                    .append("untuk sertifikat Asuransi Jiwa Kredit atas nama ")
                    .append(namaTertanggung)
                    .append(" dengan nomor sertifikat ")
                    .append(noSertifikat)
                    .append(" yang telah diterbitkan pada tanggal ")
                    .append(tanggalTerbitSertifikat)
                    .append(".")
                .append("</div>");

        emailContent.append("<div style=\"margin:0 0 12px 0;\">")
                    .append("Sampai dengan hari ini, untuk pembayaran premi atas sertifikat tersebut belum kami terima.<br>\n")
                    .append("Sehubungan dengan hal tersebut, kami mohon agar pembayaran premi dapat segera diproses guna menjamin keberlangsungan manfaat perlindungan asuransi bagi tertanggung.\n")
                .append("</div>");

        emailContent.append("<div style=\"margin:0 0 12px 0;\">")
                    .append("Informasi lebih lanjut terkait detail pembayaran terlampir pada surat yang kami kirimkan bersama email ini.")
                .append("</div>");

        emailContent.append("<div style=\"margin:0 0 12px 0;\">")
                    .append("Apabila premi telah dibayarkan, mohon kesediaannya untuk mengirimkan bukti pembayaran ke email ")
                    .append("<a href=\"mailto:collectionmri@msiglife.co.id\" style=\"color:#1a73e8;text-decoration:underline;\">collectionmri@msiglife.co.id</a>.")
                .append("</div>");

        emailContent.append("<div style=\"height:16px;line-height:16px;mso-line-height-rule:exactly;font-size:0;\">&nbsp;</div>");

        emailContent.append("<div style=\"margin:0 0 12px 0;\">")
                    .append("Demikian kami sampaikan. Atas perhatian dan kerja sama Bapak/Ibu, kami ucapkan terima kasih.")
                .append("</div>");

        emailContent.append("<div style=\"height:16px;line-height:16px;mso-line-height-rule:exactly;font-size:0;\">&nbsp;</div>");
        emailContent.append("<div style=\"margin:0;\">Credit Life Billing and Collection</div>");
        // Close inner
        emailContent.append("            </td>");
        emailContent.append("          </tr>");
        emailContent.append("        </table>");

        // Close outer
        emailContent.append("      </td>");
        emailContent.append("    </tr>");
        emailContent.append("  </table>");

        emailContent.append("</body>");
        emailContent.append("</html>");

        return emailContent.toString();
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
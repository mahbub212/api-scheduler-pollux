package id.co.msiglife.pollux.schduler.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import id.co.msiglife.pollux.schduler.dto.SuratAutoCancelDTO;
import id.co.msiglife.pollux.schduler.entity.MasterEmail;
import id.co.msiglife.pollux.schduler.repository.MasterEmailRepository;
import id.co.msiglife.pollux.schduler.repository.MasterPolicyRepository;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static id.co.msiglife.pollux.schduler.util.ConvertUtil.convertDateToJakartaTime;
import static id.co.msiglife.pollux.schduler.util.ConvertUtil.currencyFormatID;
import static id.co.msiglife.pollux.schduler.util.ConvertUtil.*;

@Service
public class SuratTunggakanPremiService {

    @Value("${spring.custom.storage.path}")
    private String STORAGE_PATH;

    @Value("${spring.custom.mailpool-attachment.path}")
    private String ATTACHMENT_PATH;

    @Autowired
    MasterPolicyRepository masterPolicyRepository;

    @Autowired
    private FileProcessUtil fileProcessUtil;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    MasterEmailRepository masterEmailRepository;

    //@Scheduled(cron = "0 0 22 * * ?")
    @Scheduled(cron = "0 */10 * * * ?")
    public void sendEmailTunggakanPremi(){
        List<SuratAutoCancelDTO> tunggakanPremiDTOList = getTunggakanPremi(StaticValue.TUNGGAKAN_PREMI_REMINDER_DAYS);
        System.out.println("=================== START GENERATE PDF ===================");
        generatePdfTunggakanPremi(tunggakanPremiDTOList);
        System.out.println("=================== END GENERATED PDF ===================");
        System.out.println("=================== START SENDING EMAIL PDF ===================");
        generateAutoMail(tunggakanPremiDTOList);
        System.out.println("=================== END SEND EMAIL PDF ===================");
    }

    private void generateAutoMail(List<SuratAutoCancelDTO> tunggakanPremiDTOList) {

        for (SuratAutoCancelDTO dto : tunggakanPremiDTOList) {
            try {
                Date sysDate = (Date) entityManager.createNativeQuery("SELECT SYSDATE FROM DUAL").getSingleResult();

                String sourceFilePath = STORAGE_PATH + "/" + dto.getRegSpaj()  + "/" + dto.getRegSpaj() + "_SURAT_TUNGGAKAN_PREMI.pdf";
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

                        String destinationFilePath = attachmentFolderPath + "/" + dto.getRegSpaj() + "_SURAT_TUNGGAKAN_PREMI.pdf";
                        Path destinationPath = Paths.get(destinationFilePath).toAbsolutePath();
                        Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                        if (!Files.isReadable(destinationPath)) {
                            try {
                                throw new IOException("The copied Surat Tunggakan Premi file is not readable: " + destinationFilePath);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        // set emailTo, subject, message, etc.
                        String emailTo = stringBlank(dto.getEmailAo()) ? "" : dto.getEmailAo() + ";" + (stringBlank(dto.getEmailCabang()) ? "" : dto.getEmailCabang());

                        String subject = "Surat Tunggakan Premi Asuransi Jiwa Kredit a/n " + dto.getNamaTertanggung() + " No Sertifikat " + dto.getNomorSertifikat();
                        String message = templateMessageTunggakanPremi(dto.getNamaBankCabang(), dto.getNamaTertanggung(), dto.getNomorSertifikat(), convertDateToJakartaTime(dto.getTanggalCetakSertifikat()));

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

                        masterEmailRepository.save(emailEntity);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    throw new RuntimeException("File Attachment not exist");
                }

            } catch (Exception e) {
                throw new RuntimeException("Error when get File Attachment");
            }
        }
    }

    private void generatePdfTunggakanPremi(List<SuratAutoCancelDTO> suratAutoCancelDTOList){

        try {

            String today = convertDateToJakartaTime(LocalDate.now());

            // read template file
            String filePath = "template/surat_tunggakan_premi_template.html";
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

            String base64Font = fileProcessUtil.encodeFontToBase64("template/fonts/Ubuntu/Ubuntu-Regular.ttf");
            content = content.replace("[[MY_FONT_PATH]]", base64Font);

            String imgPathWTHeader = fileProcessUtil.getBase64Image("template/images/watermark_header.png");
            content = content.replace("../../images/watermark_header.png", imgPathWTHeader);

            String imgPathWt = fileProcessUtil.getBase64Image("template/images/watermark.png");
            content = content.replace("../../images/watermark.png", imgPathWt);

            String imgPathFileFooter1 = fileProcessUtil.getBase64Image("template/images/watermark_footer1.png");
            content = content.replace("../../images/watermark_footer1.png", imgPathFileFooter1);

            String imgPathFileFooter2 = fileProcessUtil.getBase64Image("template/images/watermark_footer2.png");
            content = content.replace("../../images/watermark_footer2.png", imgPathFileFooter2);

            for (SuratAutoCancelDTO dto : suratAutoCancelDTOList ){

                String editedContent = content;

                editedContent = editedContent.replace("{nomor_sertifikat}", dto.getNomorSertifikat());
                editedContent = editedContent.replace("{tanggal_terbit_tunggakan}", today);
                editedContent = editedContent.replace("{nama_cabang_bank}", dto.getNamaBankCabang());
                editedContent = editedContent.replace("{nama_tertanggung}", dto.getNamaTertanggung());
                editedContent = editedContent.replace("{tanggal_lahir_tertanggung}", convertDateToJakartaTime(dto.getTanggalLahirTertanggung()));
                editedContent = editedContent.replace("{usia_tertanggung}", dto.getUsiaTertanggung().toString());
                editedContent = editedContent.replace("{uang_pertanggungan}", currencyFormatID(dto.getUangPertanggungan()));
                editedContent = editedContent.replace("{tenor_tahun}", dto.getMasaAsuransiTahun().toString());
                editedContent = editedContent.replace("{tenor_bulan}", dto.getMasaAsuransiBulan().toString());
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
                String outputPdf = folderPath + "/" + dto.getRegSpaj() + "_SURAT_TUNGGAKAN_PREMI.pdf";

                try (OutputStream os = Files.newOutputStream(Paths.get(outputPdf))) {
                    PdfRendererBuilder builder = new PdfRendererBuilder();
                    builder.withUri(outputPdf);
                    builder.toStream(os);
                    builder.withW3cDocument(new W3CDom().fromJsoup(document), folderPath + "/");
                    builder.run();
                }

                fileProcessUtil.createOrUpdateFileMasterUploadDokumen(dto.getRegSpaj(), 17, outputPdf, 0);

            }

        }catch (IOException e){
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    private String templateMessageTunggakanPremi(String namaBankCabang, String namaTertanggung, String nomorSertifikat, String tanggalTerbit) {
        StringBuilder emailContent = new StringBuilder();

        emailContent.append("<!DOCTYPE html>");
        emailContent.append("<html lang=\"id\">");
        emailContent.append("<head>");
        emailContent.append("<meta charset=\"UTF-8\">");
        emailContent.append("<title>Surat Tunggakan Premi</title>");
        emailContent.append("</head>");
        emailContent.append("<body style=\"font-family: Arial, sans-serif; font-size: 14px; color: #000000;\">");

        emailContent.append("<p>Kepada Yth.<br><strong>")
                .append(namaBankCabang)
                .append("</strong></p>");

        emailContent.append("<p>Kami mengucapkan terima kasih kepada Bapak/Ibu yang telah mempercayakan kami, ")
                .append("PT MSIG Life Insurance Indonesia Tbk. untuk memberikan perlindungan Asuransi Jiwa Kredit kepada Bapak/Ibu.</p>");

        emailContent.append("<p>Berikut kami sampaikan <strong>SURAT TUNGGAKAN PREMI ASURANSI JIWA KREDIT</strong> ")
                .append("untuk sertifikat Asuransi Jiwa Kredit atas nama <strong>")
                .append(namaTertanggung)
                .append("</strong> dengan nomor sertifikat <strong>")
                .append(nomorSertifikat)
                .append("</strong> yang telah diterbitkan pada tanggal <strong>")
                .append(tanggalTerbit)
                .append("</strong>.</p>");

        emailContent.append("<p>Sampai dengan hari ini, untuk pembayaran premi atas sertifikat tersebut belum kami terima.<br>");
        emailContent.append("Sehubungan dengan hal tersebut, kami mohon agar pembayaran premi dapat segera diproses guna menjamin keberlangsungan manfaat perlindungan asuransi bagi tertanggung.</p>");

        emailContent.append("<p>Informasi lebih lanjut terkait detail pembayaran terlampir pada surat yang kami kirimkan bersama email ini.</p>");

        emailContent.append("<p>Apabila premi telah dibayarkan, mohon kesediaannya untuk mengirimkan bukti pembayaran ke email ");
        emailContent.append("<a href=\"mailto:collectionmri@msiglife.co.id\" style=\"color: #0066cc;\">collectionmri@msiglife.co.id</a></p>");

        emailContent.append("<p>Demikian kami sampaikan. Atas perhatian dan kerja sama Bapak/Ibu, kami ucapkan terima kasih.</p>");

        emailContent.append("<p><strong>Credit Life Billing and Collection</strong></p>");

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

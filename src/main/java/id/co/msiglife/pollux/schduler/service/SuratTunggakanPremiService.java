package id.co.msiglife.pollux.schduler.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import id.co.msiglife.pollux.schduler.dto.SuratAutoCancelDTO;
import id.co.msiglife.pollux.schduler.entity.*;
import id.co.msiglife.pollux.schduler.exception.NotFoundException;
import id.co.msiglife.pollux.schduler.repository.*;
import id.co.msiglife.pollux.schduler.util.ConvertUtil;
import id.co.msiglife.pollux.schduler.util.FileProcessUtil;
import id.co.msiglife.pollux.schduler.util.StaticValue;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static id.co.msiglife.pollux.schduler.util.ConvertUtil.convertDateToJakartaTime;
import static id.co.msiglife.pollux.schduler.util.ConvertUtil.currencyFormatID;
import static id.co.msiglife.pollux.schduler.util.ConvertUtil.*;

@Service
public class SuratTunggakanPremiService {

    @Value("${spring.custom.storage.path}")
    private String STORAGE_PATH;

    @Value("${spring.custom.mailpool-attachment.path}")
    private String ATTACHMENT_PATH;

    @Value("${spring.custom.syariah-document.signer-name}")
    private String SYARIAH_SIGNER_NAME;

    @Value("${spring.custom.syariah-document.signer-position}")
    private String SYARIAH_SIGNER_POSITION;

    @Autowired
    MasterPolicyRepository masterPolicyRepository;

    @Autowired
    private FileProcessUtil fileProcessUtil;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    MasterEmailRepository masterEmailRepository;

    @Autowired
    MasterAutoCancelEmailLogRepository masterAutoCancelEmailLogRepository;

    @Autowired
    MasterBranchPartnerRepository masterBranchPartnerRepository;

    @Autowired
    MasterPKSRepository masterPKSRepository;

    public void generateAutoMail(List<SuratAutoCancelDTO> tunggakanPremiDTOList) {
        int errorIn = 3;
        int iteration = 0;
        for (SuratAutoCancelDTO dto : tunggakanPremiDTOList) {
            try {
                Date sysDate = (Date) entityManager.createNativeQuery("SELECT SYSDATE FROM DUAL").getSingleResult();

                String sourceFilePath = STORAGE_PATH + "/" + dto.getRegSpaj()  + "/" + dto.getRegSpaj() + "_SURAT_TUNGGAKAN_PREMI.pdf";
                Path sourcePath = Paths.get(sourceFilePath).toAbsolutePath();

                String generatedId = entityManager.createNativeQuery("SELECT EKA.PAC_COUNTER.GET_EMAIL_ID FROM DUAL").getSingleResult().toString();
                String attachmentFolderPath = ATTACHMENT_PATH + generatedId.substring(0, 4) + "/" + generatedId.substring(4, 6) + "/" + generatedId;
                /*
                System.out.println(iteration);
                if(iteration == errorIn){
                    iteration++;
                    throw new RuntimeException("Coba break");
                }
                 */
                iteration++;
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
                                System.err.println("Failed to generate PDF for regSpaj: " + dto.getRegSpaj() + " | Error: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }

                        MasterPolicy masterPolicy = masterPolicyRepository.findByRegSpaj(dto.getRegSpaj());
                        if (masterPolicy == null) {
                            throw new NotFoundException("Data Master Policy not found");
                        }

                        MasterPKS masterPKS = masterPKSRepository.findByPksId(masterPolicy.getPksId());
                        if (masterPKS == null) {
                            throw new NotFoundException("Data PKS not found");
                        }

                        System.out.println("PKS ID = " + masterPolicy.getPksId());
                        MasterBranchPartner masterBranchPartner = masterBranchPartnerRepository.findByMclId(masterPKS.getMclId());
                        if (masterBranchPartner == null) {
                            throw new NotFoundException("Data Master Branch Partner not found");
                        }

                        // Set emailTo
                        StringBuilder emailBuilder = new StringBuilder();

                        //AO_Name
                        if (!stringBlank(masterBranchPartner.getEmailAo())) {
                            emailBuilder.append(masterBranchPartner.getEmailAo()).append(";");
                         }

                         //Email_Cab
                         if (!stringBlank(masterBranchPartner.getEmailCab())) {
                            emailBuilder.append(masterBranchPartner.getEmailCab()).append(";");
                         }

                        if (emailBuilder.length() == 0) {

                            if (!stringBlank(dto.getEmailAo())) {
                                emailBuilder.append(dto.getEmailAo()).append(";");
                            }

                            if (!stringBlank(dto.getEmailCabang())) {
                                emailBuilder.append(dto.getEmailCabang()).append(";");
                            }

                            if (!stringBlank(dto.getEmailPic())) {
                                emailBuilder.append(dto.getEmailPic()).append(";");
                            }
                        }

                        String emailTo = emailBuilder.toString();

                        if (emailTo.endsWith(";")) {
                            emailTo = emailTo.substring(0, emailTo.length() - 1);
                        }

                        System.out.println("Email To = " + emailTo);

                        String subject = "";
                        String message = "";
                        if(dto.isSyariah()){
                            subject = "Surat Tunggakan Pembayaran Kontribusi a/n " + dto.getNamaTertanggung() + " No Sertifikat " + dto.getNomorSertifikat();
                            message = templateMessageTunggakanPremiSyariah(dto.getNamaBankCabang(), dto.getNamaTertanggung(), dto.getNomorSertifikat(), convertDateToJakartaTime(dto.getTanggalCetakSertifikat()));
                        } else {
                            subject = "Surat Tunggakan Premi Asuransi Jiwa Kredit a/n " + dto.getNamaTertanggung() + " No Sertifikat " + dto.getNomorSertifikat();
                            message = templateMessageTunggakanPremi(dto.getNamaBankCabang(), dto.getNamaTertanggung(), dto.getNomorSertifikat(), convertDateToJakartaTime(dto.getTanggalCetakSertifikat()));
                        }

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
                        emailEntity.setPathAttach(destinationPath.toString());

                        masterEmailRepository.save(emailEntity);

                        String savedId = emailEntity.getId();
                        MasterAutoCancelEmailLog masterAutoCancelEmailLog = masterAutoCancelEmailLogRepository.findByRegSpajAndReminderType(dto.getRegSpaj(), StaticValue.TUNGGAKAN_PREMI_REMINDER_TYPE);

                        if (masterAutoCancelEmailLog == null) {
                            masterAutoCancelEmailLog = new MasterAutoCancelEmailLog();
                            masterAutoCancelEmailLog.setReminderDays(StaticValue.TUNGGAKAN_PREMI_REMINDER_DAYS);
                        }

                        masterAutoCancelEmailLog.setMeId(savedId);
                        masterAutoCancelEmailLog.setRegSpaj(dto.getRegSpaj());
                        masterAutoCancelEmailLog.setPathAttach(destinationPath.toString());
                        masterAutoCancelEmailLog.setUpdatedAt(sysDate);
                        masterAutoCancelEmailLog.setFlagDone(1);
                        masterAutoCancelEmailLog.setFlagPolisCanceled(0);
                        masterAutoCancelEmailLog.setReminderType(StaticValue.TUNGGAKAN_PREMI_REMINDER_TYPE);

                        masterAutoCancelEmailLogRepository.save(masterAutoCancelEmailLog);

                    } catch (IOException e) {
                        System.err.println("Failed to generate PDF for regSpaj: " + dto.getRegSpaj() + " | Error: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    throw new RuntimeException("File Attachment not exist");
                }

            } catch (Exception e) {
                System.err.println("Error when getting file attachment");
                e.printStackTrace();
            }
        }
    }

    public void generatePdfTunggakanPremi(List<SuratAutoCancelDTO> suratAutoCancelDTOList){

        String today = convertDateToJakartaTime(LocalDate.now());

        String headerKonven = "template/images/watermark_header.png";
        String footerKonven1 = "template/images/watermark_footer1.png";
        String footerKonven2 = "template/images/watermark_footer2.png";
        String headerSyariah = "template/images/watermark_header_syariah.png";
        String footerSyariah1 = "template/images/watermark_footer1_syariah_2.png";
        String footerSyariah2 = "template/images/watermark_footer1_syariah_3.png";

        String templateSyariah = initTemplateResource(loadTemplate(getTemplatePath(true)), headerSyariah, footerSyariah1, footerSyariah2);
        String templateKonven = initTemplateResource(loadTemplate(getTemplatePath(false)), headerKonven, footerKonven1, footerKonven2);

        for (SuratAutoCancelDTO dto : suratAutoCancelDTOList) {

            if (dto.isSyariah()) {
                processSuratTunggakanPremi(dto, templateSyariah, today);
            } else {
                //processSuratTunggakanPremi(dto, templateKonven, today);
            }

        }
    }

    private void processSuratTunggakanPremi(SuratAutoCancelDTO dto, String content, String today) {

        String editedContent = content; // reset content to template state

        try {
            Date tanggalCetak = dto.getTanggalCetakSertifikat();
            LocalDate localDate = tanggalCetak.toInstant()
                    .atZone(ZoneId.of("Asia/Jakarta"))
                    .toLocalDate();

            String thirtyDaysLater = convertDateToJakartaTime(localDate.plusDays(30));

            // create the folder if it doesn't exist
            String folderPath = STORAGE_PATH + dto.getRegSpaj();

            // PdfRendererBuilder will take this XHTML document and create a PDF as the output file
            String outputPdf = folderPath + "/" + dto.getRegSpaj() + "_SURAT_TUNGGAKAN_PREMI.pdf";
            Path sourcePath = Paths.get(outputPdf).toAbsolutePath();
            System.out.println("Checking file existence at path: " + outputPdf);
            System.out.println("Absolute path: " + Paths.get(outputPdf).toAbsolutePath());
            System.out.println("Exists? " + Files.exists(Paths.get(outputPdf)));
            System.out.println("Exists path? " + Files.exists(sourcePath));

            if (!Files.exists(sourcePath)) {

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
                editedContent = editedContent.replace("{tanggal_akhir_pembayaran_premi}", thirtyDaysLater);

                // signature
                if (dto.isSyariah()) {
                    editedContent = editedContent.replace("{signer_name}", SYARIAH_SIGNER_NAME);
                    editedContent = editedContent.replace("{signer_position}", SYARIAH_SIGNER_POSITION);
                }

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

                try (OutputStream os = Files.newOutputStream(sourcePath)) {
                    PdfRendererBuilder builder = new PdfRendererBuilder();
                    builder.withUri(sourcePath.toString());
                    builder.toStream(os);
                    builder.withW3cDocument(new W3CDom().fromJsoup(document), folderPath + "/");
                    builder.run();
                }

                fileProcessUtil.createOrUpdateFileMasterUploadDokumen(dto.getRegSpaj(), 17, outputPdf, 0);
            } else {
                System.out.println("Skipping PDF generation; file already exists for regSpaj: " + dto.getRegSpaj());
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private String getTemplatePath(boolean isSyariah) {
        return isSyariah
                ? "template/syariahTemplate/surat_tunggakan_premi_syariah_template.html"
                : "template/surat_tunggakan_premi_template.html";
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

    private String templateMessageTunggakanPremi(String namaBankCabang, String namaTertanggung, String nomorSertifikat, String tanggalTerbit) {
        StringBuilder emailContent = new StringBuilder();

        emailContent.append("<!DOCTYPE html>");
        emailContent.append("<html lang=\"id\">");
        emailContent.append("  <head>");
        emailContent.append("    <meta http-equiv=\"x-ua-compatible\" content=\"ie=edge\">");
        emailContent.append("    <meta charset=\"utf-8\">");
        emailContent.append("    <meta name=\"x-apple-disable-message-reformatting\">");
        emailContent.append("    <title>Surat Tunggakan Premi</title>");
        emailContent.append("  </head>");
        emailContent.append("<body style=\"font-family: Arial, sans-serif; font-size: 14px; color: #000000;\">");
        emailContent.append("    <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"background:#ffffff;\">");
        emailContent.append("      <tr>");
        emailContent.append("        <td align=\"left\">");
        emailContent.append("          <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:100%;\">");
        emailContent.append("            <tr>");
        emailContent.append("              <td style=\"font-family:Arial,Helvetica,sans-serif;font-size:14px;line-height:22px;color:#000000;padding:16px;\">");
        emailContent.append("               <div style=\"margin:0 0 12px 0;\">")
                    .append("Kepada Yth.")
                    .append("<br>")
                    .append("<strong>")
                    .append(namaBankCabang)
                    .append("</strong>")
                    .append("</div>");

        emailContent.append("<div style=\"margin:0 0 12px 0;\">")
                    .append("Kami mengucapkan terima kasih kepada Bapak/Ibu yang telah mempercayakan kami, ")
                    .append("PT MSIG Life Insurance Indonesia Tbk. untuk memberikan perlindungan Asuransi Jiwa Kredit kepada Bapak/Ibu.")
                .append("</div>");

        emailContent.append("<div style=\"margin:0 0 12px 0;\">")
                    .append("Berikut kami sampaikan <strong>SURAT TUNGGAKAN PREMI ASURANSI JIWA KREDIT</strong>")
                    .append(" untuk sertifikat Asuransi Jiwa Kredit atas nama ")
                    .append(namaTertanggung)
                    .append(" dengan nomor sertifikat ")
                    .append(nomorSertifikat)
                    .append(" yang telah diterbitkan pada tanggal ")
                    .append(tanggalTerbit)
                .append("</div>");

        emailContent.append("<div style=\"margin:0 0 12px 0;\">")
                    .append("Sampai dengan hari ini, untuk pembayaran premi atas sertifikat tersebut belum kami terima.<br>")
                    .append("Sehubungan dengan hal tersebut, kami mohon agar pembayaran premi dapat segera diproses guna menjamin keberlangsungan manfaat perlindungan asuransi bagi tertanggung.")
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

        emailContent.append("              <div style=\"margin:0;\">Credit Life Billing and Collection</div>");
        emailContent.append("              </td>");
        emailContent.append("            </tr>");
        emailContent.append("          </table>");
        emailContent.append("        </td>");
        emailContent.append("      </tr>");
        emailContent.append("    </table>");
        emailContent.append("</body>");
        emailContent.append("</html>");

        return emailContent.toString();
    }

    private String templateMessageTunggakanPremiSyariah(String namaBankCabang, String namaTertanggung, String nomorSertifikat, String tanggalTerbit) {
        StringBuilder emailContent = new StringBuilder();

        emailContent.append("<!DOCTYPE html>");
        emailContent.append("<html lang=\"id\">");
        emailContent.append("  <head>");
        emailContent.append("    <meta http-equiv=\"x-ua-compatible\" content=\"ie=edge\">");
        emailContent.append("    <meta charset=\"utf-8\">");
        emailContent.append("    <meta name=\"x-apple-disable-message-reformatting\">");
        emailContent.append("    <title>Surat Tunggakan Kontribusi</title>");
        emailContent.append("  </head>");
        emailContent.append("<body style=\"font-family: Arial, sans-serif; font-size: 14px; color: #000000;\">");
        emailContent.append("    <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"background:#ffffff;\">");
        emailContent.append("      <tr>");
        emailContent.append("        <td align=\"left\">");
        emailContent.append("          <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:100%;\">");
        emailContent.append("            <tr>");
        emailContent.append("              <td style=\"font-family:Arial,Helvetica,sans-serif;font-size:14px;line-height:22px;color:#000000;padding:16px;\">");
        emailContent.append("               <div style=\"margin:0 0 12px 0;\">")
                .append("Kepada Yth.")
                .append("<br>")
                .append("<strong>")
                .append(namaBankCabang)
                .append("</strong>")
                .append("</div>");

        emailContent.append("<div style=\"margin:0 0 12px 0;\">")
                .append("Kami mengucapkan terima kasih atas kepercayaan yang Bapak/Ibu berikan kepada PT MSIG Sharia Life Insurance Indonesia untuk mengelola dana kontribusi peserta, sesuai prinsip Syariah, guna saling menolong dan melindungi Anda serta keluarga.")
                .append("</div>");

        emailContent.append("<div style=\"margin:0 0 12px 0;\">")
                .append("Berikut kami sampaikan <strong>SURAT TUNGGAKAN PEMBAYARAN KONTRIBUSI ASURANSI JIWA PEMBIAYAAN SYARIAH</strong>")
                .append(" untuk sertifikat Asuransi Jiwa Pembiayaan Syariah atas nama ")
                .append(namaTertanggung)
                .append(" dengan nomor sertifikat ")
                .append(nomorSertifikat)
                .append(" yang telah diterbitkan pada tanggal ")
                .append(tanggalTerbit)
                .append("</div>");

        emailContent.append("<div style=\"margin:0 0 12px 0;\">")
                .append("Sampai dengan hari ini, untuk pembayaran kontribusi atas sertifikat tersebut belum kami terima.<br>")
                .append("Sehubungan dengan hal tersebut, kami mohon agar pembayaran kontribusi dapat segera diproses guna menjamin keberlangsungan manfaat perlindungan asuransi bagi peserta.")
                .append("</div>");

        emailContent.append("<div style=\"margin:0 0 12px 0;\">")
                .append("Informasi lebih lanjut terkait detail pembayaran terlampir pada surat yang kami kirimkan bersama email ini.")
                .append("</div>");

        emailContent.append("<div style=\"margin:0 0 12px 0;\">")
                .append("Apabila kontribusi telah dibayarkan, mohon kesediaannya untuk mengirimkan bukti pembayaran ke email ")
                .append("<a href=\"mailto:collectionmri@msiglife.co.id\" style=\"color:#1a73e8;text-decoration:underline;\">collectionmri@msiglife.co.id</a>.")
                .append("</div>");

        emailContent.append("<div style=\"height:16px;line-height:16px;mso-line-height-rule:exactly;font-size:0;\">&nbsp;</div>");

        emailContent.append("<div style=\"margin:0 0 12px 0;\">")
                .append("Demikian kami sampaikan. Atas perhatian dan kerja sama Bapak/Ibu, kami ucapkan terima kasih.")
                .append("</div>");

        emailContent.append("<div style=\"height:16px;line-height:16px;mso-line-height-rule:exactly;font-size:0;\">&nbsp;</div>");

        emailContent.append("              <div style=\"margin:0;\">Credit Life Billing and Collection<br>PT MSIG Sharia Life Insurance</div>");
        emailContent.append("              </td>");
        emailContent.append("            </tr>");
        emailContent.append("          </table>");
        emailContent.append("        </td>");
        emailContent.append("      </tr>");
        emailContent.append("    </table>");
        emailContent.append("</body>");
        emailContent.append("</html>");

        return emailContent.toString();
    }

    public List<SuratAutoCancelDTO> getTunggakanPremi(Integer reminderType) {
        List<Object[]> listData = masterPolicyRepository.findAutoCancelByReminderType(reminderType);
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
            dto.setEmailPic(ConvertUtil.safeString(row[17]));
            result.add(dto);
        }

        return result;
    }

}

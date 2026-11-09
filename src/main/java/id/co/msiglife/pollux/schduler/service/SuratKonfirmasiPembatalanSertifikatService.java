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
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static id.co.msiglife.pollux.schduler.util.ConvertUtil.*;
import static java.util.Arrays.stream;

@Service
public class SuratKonfirmasiPembatalanSertifikatService {

    @Autowired
    MasterEmailRepository masterEmailRepository;

    @Autowired
    MasterPolicyRepository masterPolicyRepository;

    @Autowired
    MasterAutoCancelEmailLogRepository masterAutoCancelEmailLogRepository;

    @Autowired
    @Lazy
    private SuratKonfirmasiPembatalanSertifikatService self;

    @Autowired
    private FileProcessUtil fileProcessUtil;

    @Autowired
    MasterBranchPartnerRepository masterBranchPartnerRepository;

    @Autowired
    MasterPKSRepository masterPKSRepository;

    @Value("${spring.custom.mailpool-attachment.path}")
    private String ATTACHMENT_PATH;

    @Value("${spring.custom.storage.path}")
    private String STORAGE_PATH;

    @Value("${spring.custom.syariah-document.signer-name}")
    private String SYARIAH_SIGNER_NAME;

    @Value("${spring.custom.syariah-document.signer-position}")
    private String SYARIAH_SIGNER_POSITION;

    @Value("${spring.custom.syariah-document.signature-image}")
    private String SYARIAH_SIGNATURE_IMAGE;

    @PersistenceContext
    private EntityManager entityManager;

    public void generateEmailSuratPembatalanSertifikat(List<SuratAutoCancelDTO> pembatalanSertifikatDTOList) {
        if (pembatalanSertifikatDTOList == null || pembatalanSertifikatDTOList.isEmpty()) return;

        for (SuratAutoCancelDTO dto : pembatalanSertifikatDTOList) {
            try {
                // Dipanggil via 'self' proxy agar @Transactional(REQUIRES_NEW) AKTIF
                self.processSingleEmailPembatalan(dto);

            } catch (Exception e) {
                System.err.println("Gagal memproses email regSpaj " + dto.getRegSpaj() + ", skip ke data berikutnya. Error: " + e.getMessage());
            }
        }
    }

    // rollbackFor = Exception.class memastikan semua jenis Exception memicu Rollback
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void processSingleEmailPembatalan(SuratAutoCancelDTO dto) throws Exception {

        Date sysDate = (Date) entityManager.createNativeQuery("SELECT SYSDATE FROM DUAL").getSingleResult();

        LocalDate localDate = dto.getTanggalCetakSertifikat().toInstant()
                .atZone(ZoneId.of("Asia/Jakarta"))
                .toLocalDate();
        String thirtyDaysLater = convertDateToJakartaTime(localDate.plusDays(30));

        String sourceFilePath = STORAGE_PATH + "/" + dto.getRegSpaj() + "/" + dto.getRegSpaj() + "_SURAT_KONFIRMASI_PEMBATALAN_SERTIFIKAT.pdf";
        Path sourcePath = Paths.get(sourceFilePath).toAbsolutePath();

        if (!Files.exists(sourcePath) || !Files.isReadable(sourcePath)) {
            throw new FileNotFoundException("File Attachment tidak ditemukan/readable: " + sourceFilePath);
        }

        String generatedId = entityManager.createNativeQuery("SELECT EKA.PAC_COUNTER.GET_EMAIL_ID FROM DUAL").getSingleResult().toString();
        String attachmentFolderPath = ATTACHMENT_PATH + generatedId.substring(0, 4) + "/" + generatedId.substring(4, 6) + "/" + generatedId;

        File folder = new File(attachmentFolderPath);
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IOException("Failed to create directory: " + attachmentFolderPath);
        }

        String destinationFilePath = attachmentFolderPath + "/" + dto.getRegSpaj() + "_SURAT_KONFIRMASI_PEMBATALAN_SERTIFIKAT.pdf";
        Path destinationPath = Paths.get(destinationFilePath).toAbsolutePath();

        Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);

        if (!Files.isReadable(destinationPath)) {
            throw new IOException("The copied file is not readable: " + destinationFilePath);
        }

        MasterPolicy masterPolicy = masterPolicyRepository.findByRegSpaj(dto.getRegSpaj());
        if (masterPolicy == null) {
            throw new NotFoundException("Data Master Policy not found");
        }

        MasterPKS masterPKS = masterPKSRepository.findByPksId(masterPolicy.getPksId());
        if (masterPKS == null) {
            throw new NotFoundException("Data PKS not found");
        }

        System.out.println("MSPO Customer = " + masterPolicy.getCustomer());
        MasterBranchPartner masterBranchPartner = masterBranchPartnerRepository.findByMclId(masterPolicy.getCustomer());

        StringBuilder emailBuilder = new StringBuilder();
        if (masterBranchPartner == null) {
            throw new NotFoundException("Data Master Branch Partner not found");
        } else {
            //AO_Name
            if (!stringBlank(masterBranchPartner.getEmailAo())) {
                emailBuilder.append(masterBranchPartner.getEmailAo()).append(";");
            }
            //Email_Cab
            if (!stringBlank(masterBranchPartner.getEmailCab())) {
                emailBuilder.append(masterBranchPartner.getEmailCab()).append(";");
            }
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

        emailTo = stream(emailBuilder.toString().split(";"))
                .map(String::trim)
                .filter(email -> !email.isEmpty())
                .distinct()
                .collect(Collectors.joining(";"));

        System.out.println("Email To = " + emailTo);

        String subject = "Surat Konfirmasi Pembatalan Sertifikat a/n " + dto.getNamaTertanggung() + " No Sertifikat " + dto.getNomorSertifikat();
        String message = dto.isSyariah()
                ? templateSuratKonfirmasiPembatalanSertifikatEmailSyariah(dto.getNamaBankCabang(), dto.getNamaTertanggung(), dto.getNomorSertifikat(), convertDateToJakartaTime(dto.getTanggalCetakSertifikat()), thirtyDaysLater)
                : templateSuratKonfirmasiPembatalanSertifikatEmail(dto.getNamaBankCabang(), dto.getNamaTertanggung(), dto.getNomorSertifikat(), convertDateToJakartaTime(dto.getTanggalCetakSertifikat()), thirtyDaysLater);

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

        MasterAutoCancelEmailLog masterAutoCancelEmailLog = masterAutoCancelEmailLogRepository
                .findByRegSpajAndReminderType(dto.getRegSpaj(), StaticValue.SERTIFIKAT_DIBATALKAN_NOTIFICATION_TYPE);

        if (masterAutoCancelEmailLog == null) {
            masterAutoCancelEmailLog = new MasterAutoCancelEmailLog();
            masterAutoCancelEmailLog.setReminderDays(StaticValue.SERTIFIKAT_DIBATALKAN_NOTIFICATION_DAYS);
        }

        masterAutoCancelEmailLog.setMeId(generatedId);
        masterAutoCancelEmailLog.setRegSpaj(dto.getRegSpaj());
        masterAutoCancelEmailLog.setPathAttach(destinationPath.toString());
        masterAutoCancelEmailLog.setUpdatedAt(sysDate);
        masterAutoCancelEmailLog.setFlagDone(1);
        masterAutoCancelEmailLog.setFlagPolisCanceled(0);
        masterAutoCancelEmailLog.setReminderType(StaticValue.SERTIFIKAT_DIBATALKAN_NOTIFICATION_TYPE);

        masterAutoCancelEmailLogRepository.save(masterAutoCancelEmailLog);
    }

    public void generatePdfSuratPembatalanSertifikat(List<SuratAutoCancelDTO> pembatalanSertifikatDTOList) {

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

        Iterator<SuratAutoCancelDTO> iterator = pembatalanSertifikatDTOList.iterator();

        while (iterator.hasNext()) {
            SuratAutoCancelDTO dto = iterator.next();
            boolean isSuccess = false;

            try {
                if (dto.isSyariah()) {
                    isSuccess = processSuratSertifikatDibatalkan(dto, templateSyariah, today);
                } else {
                    //isSuccess = processSuratSertifikatDibatalkan(dto, templateKonven, today);
                }

                // Jika proses return false (gagal/file tidak terbentuk), hapus dari list
                if (!isSuccess) {
                    System.out.println("Removing DTO from list (PDF Generation Failed): " + dto.getRegSpaj());
                    iterator.remove();
                }

            } catch (Exception e) {
                System.err.println("Error generating PDF for regSpaj " + dto.getRegSpaj() + ", removing from process list.");
                iterator.remove(); // Hapus DTO jika melempar exception
            }
        }
    }

    private String getTemplatePath(boolean isSyariah) {
        return isSyariah
                ? "template/syariahTemplate/surat_konfirmasi_pembatalan_sertifikat_syariah.html"
                : "template/surat_konfirmasi_pembatalan_sertifikat.html";
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

    private boolean processSuratSertifikatDibatalkan(SuratAutoCancelDTO dto, String content, String today) {
        try {
            String editedContent = content; // reset content to template state

            Date tanggalCetak = dto.getTanggalCetakSertifikat();
            LocalDate localDate = tanggalCetak.toInstant()
                    .atZone(ZoneId.of("Asia/Jakarta"))
                    .toLocalDate();

            String thirtyDaysLater = convertDateToJakartaTime(localDate.plusDays(30));

            // define folderPath and outputPdf, then check if related file is already exists
            String folderPath = STORAGE_PATH + dto.getRegSpaj();
            String outputPdf = folderPath + "/" + dto.getRegSpaj() + "_SURAT_KONFIRMASI_PEMBATALAN_SERTIFIKAT.pdf";
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
                editedContent = editedContent.replace("{tenor}", dto.getMasaAsuransiTahun() + " Tahun" + (dto.getMasaAsuransiBulan() != null && dto.getMasaAsuransiBulan() > 0 ? dto.getMasaAsuransiBulan() + " Bulan" : ""));
                editedContent = editedContent.replace("{premi_asuransi}", currencyFormatID(dto.getPremi()));
                editedContent = editedContent.replace("{tanggal_cetak_sertifikat}", convertDateToJakartaTime(dto.getTanggalCetakSertifikat()));
                editedContent = editedContent.replace("{nama_bank_pks}", dto.getNamaBankPks());
                editedContent = editedContent.replace("{atas_nama_pks}", dto.getNamaRekeningPks());
                editedContent = editedContent.replace("{rekening_pks}", dto.getNomorRekeningPks());
                editedContent = editedContent.replace("{up}", currencyFormatID(dto.getUangPertanggungan()));
                editedContent = editedContent.replace("{tanggal_jatuh_tempo_pembayaran_premi}", thirtyDaysLater);

                // signature
                if (dto.isSyariah()) {
                    editedContent = editedContent.replace("{signer_name}", SYARIAH_SIGNER_NAME);
                    editedContent = editedContent.replace("{signer_position}", SYARIAH_SIGNER_POSITION);
                }

                // create string HTML to a JSoup Document to render XHTML
                Document document = Jsoup.parse(editedContent);
                document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);

                // create the folder if it doesn't exist
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
                    throw new RuntimeException(e);
                }

                // PdfRendererBuilder will take this XHTML document and create a PDF as the output file
                try (OutputStream os = Files.newOutputStream(Paths.get(outputPdf))) {
                    PdfRendererBuilder builder = new PdfRendererBuilder();
                    builder.withUri(outputPdf);
                    builder.toStream(os);
                    builder.withW3cDocument(new W3CDom().fromJsoup(document), folderPath + "/");
                    builder.run();
                } catch (IOException e) {
                    System.err.println("Failed to generate PDF for regSpaj: " + dto.getRegSpaj() + " | Error: " + e.getMessage());
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

                // save into MasterUploadDokumen
                this.fileProcessUtil.createOrUpdateFileMasterUploadDokumen(dto.getRegSpaj(), 18, outputPdf, 0);
            } else {
                System.out.println("Skipping PDF generation; file already exists for regSpaj: " + dto.getRegSpaj());
            }
            return Files.exists(sourcePath);
        } catch (Exception e) {
            System.err.println("Failed to generate PDF for regSpaj: " + dto.getRegSpaj() + " | Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String templateSuratKonfirmasiPembatalanSertifikatEmail(String namaCabang, String namaTertanggung, String noSertifikat, String tanggalTerbitSertifikat, String tanggalKemarin) {

        StringBuilder emailContent = new StringBuilder();

        emailContent.append("<!DOCTYPE html>");
        emailContent.append("<html lang=\"id\">");
        emailContent.append("<head>");
        emailContent.append("  <meta http-equiv=\"x-ua-compatible\" content=\"ie=edge\">");
        emailContent.append("  <meta charset=\"UTF-8\">");
        emailContent.append("  <meta name=\"x-apple-disable-message-reformatting\">");
        emailContent.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
        emailContent.append("  <title>Surat Konfirmasi Pembatalan Sertifikat</title>");
        emailContent.append("</head>");
        emailContent.append("<body style=\"font-family: Arial, sans-serif; font-size: 14px; color: #000000;\">");

        // Wrapper table
        emailContent.append("  <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"background:#ffffff;\">");
        emailContent.append("    <tr>");
        emailContent.append("      <td align=\"left\">");
        emailContent.append("        <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:100%;\">");
        emailContent.append("          <tr>");
        emailContent.append("            <td style=\"font-family:Arial,Helvetica,sans-serif;font-size:14px;line-height:22px;color:#000000;padding:16px;\">");

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
                .append("Berikut kami sampaikan <strong>SURAT KONFIRMASI PEMBATALAN SERTIFIKAT</strong> ")
                .append("untuk sertifikat Asuransi Jiwa Kredit atas nama ")
                .append(namaTertanggung)
                .append(" dengan nomor sertifikat ")
                .append(noSertifikat)
                .append(" yang telah diterbitkan pada tanggal ")
                .append(tanggalTerbitSertifikat)
                .append(".</div>");

        emailContent.append("<div style=\"margin:0 0 12px 0;\">")
                .append("Kami informasikan bahwa untuk pembayaran premi atas Sertifikat tersebut belum kami terima sampai dengan tanggal jatuh tempo pembayaran premi ditanggal ")
                .append(tanggalKemarin)
                .append(".</div>");

        emailContent.append("<div style=\"margin:0 0 12px 0;\">")
                .append("Informasi lebih lanjut terkait detail pembayaran terlampir pada surat yang kami kirimkan bersama email ini.")
                .append("</div>");

        emailContent.append("<div style=\"margin:0 0 12px 0;\">")
                .append("Apabila premi telah dibayarkan, mohon kesediaannya untuk mengirimkan bukti pembayaran ke email ")
                .append("<a href=\"mailto:collectionmri@msiglife.co.id\" style=\"color:#0066cc;\">collectionmri@msiglife.co.id</a>")
                .append("</div>");

        emailContent.append("<div style=\"height:16px;line-height:16px;mso-line-height-rule:exactly;font-size:0;\">&nbsp;</div>");

        emailContent.append("<div style=\"margin:0 0 12px 0;\">")
                .append("Demikian kami sampaikan. Atas perhatian dan kerja sama Bapak/Ibu, kami ucapkan terima kasih.")
                .append("</div>");

        emailContent.append("<div style=\"height:16px;line-height:16px;mso-line-height-rule:exactly;font-size:0;\">&nbsp;</div>");

        emailContent.append("<div style=\"margin:0;\">Credit Life Billing and Collection</div>");

        // Close inner table
        emailContent.append("            </td>");
        emailContent.append("          </tr>");
        emailContent.append("        </table>");
        emailContent.append("      </td>");
        emailContent.append("    </tr>");
        emailContent.append("  </table>");

        emailContent.append("</body>");
        emailContent.append("</html>");

        return emailContent.toString();
    }

    private String templateSuratKonfirmasiPembatalanSertifikatEmailSyariah(String namaCabang, String namaTertanggung, String noSertifikat, String tanggalTerbitSertifikat, String tanggalKemarin) {

        StringBuilder emailContent = new StringBuilder();

        emailContent.append("<!DOCTYPE html>");
        emailContent.append("<html lang=\"id\">");
        emailContent.append("<head>");
        emailContent.append("  <meta http-equiv=\"x-ua-compatible\" content=\"ie=edge\">");
        emailContent.append("  <meta charset=\"UTF-8\">");
        emailContent.append("  <meta name=\"x-apple-disable-message-reformatting\">");
        emailContent.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
        emailContent.append("  <title>Surat Konfirmasi Pembatalan Sertifikat</title>");
        emailContent.append("</head>");
        emailContent.append("<body style=\"font-family: Arial, sans-serif; font-size: 14px; color: #000000;\">");

        // Wrapper table
        emailContent.append("  <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"background:#ffffff;\">");
        emailContent.append("    <tr>");
        emailContent.append("      <td align=\"left\">");
        emailContent.append("        <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:100%;\">");
        emailContent.append("          <tr>");
        emailContent.append("            <td style=\"font-family:Arial,Helvetica,sans-serif;font-size:14px;line-height:22px;color:#000000;padding:16px;\">");

        // Content blocks
        emailContent.append("<div style=\"margin:0 0 12px 0;\">Kepada Yth.<br>")
                .append("<strong>")
                .append(namaCabang)
                .append("</strong>")
                .append("</div>");

        emailContent.append("<div style=\"margin:0 0 12px 0;\">")
                .append("Kami mengucapkan terima kasih atas kepercayaan yang Bapak/Ibu berikan kepada PT MSIG Sharia Life Insurance Indonesia untuk mengelola dana kontribusi peserta, sesuai prinsip Syariah, guna saling menolong dan melindungi Anda serta keluarga.")
                .append("</div>");

        emailContent.append("<div style=\"margin:0 0 12px 0;\">")
                .append("Berikut kami sampaikan <strong>SURAT KONFIRMASI PEMBATALAN SERTIFIKAT</strong> ")
                .append("untuk sertifikat Asuransi Jiwa Pembiayaan Syariah atas nama ")
                .append(namaTertanggung)
                .append(" dengan nomor sertifikat ")
                .append(noSertifikat)
                .append(" yang telah diterbitkan pada tanggal ")
                .append(tanggalTerbitSertifikat)
                .append(".</div>");

        emailContent.append("<div style=\"margin:0 0 12px 0;\">")
                .append("Kami informasikan bahwa untuk pembayaran kontribusi atas Sertifikat tersebut belum kami terima sampai dengan tanggal jatuh tempo pembayaran kontribusi ditanggal ")
                .append(tanggalKemarin)
                .append(".</div>");

        emailContent.append("<div style=\"margin:0 0 12px 0;\">")
                .append("Informasi lebih lanjut terkait detail pembayaran terlampir pada surat yang kami kirimkan bersama email ini.")
                .append("</div>");

        emailContent.append("<div style=\"margin:0 0 12px 0;\">")
                .append("Apabila kontribusi telah dibayarkan, mohon kesediaannya untuk mengirimkan bukti pembayaran ke email ")
                .append("<a href=\"mailto:collectionmri@msiglife.co.id\" style=\"color:#0066cc;\">collectionmri@msiglife.co.id</a>")
                .append("</div>");

        emailContent.append("<div style=\"height:16px;line-height:16px;mso-line-height-rule:exactly;font-size:0;\">&nbsp;</div>");

        emailContent.append("<div style=\"margin:0 0 12px 0;\">")
                .append("Demikian kami sampaikan. Atas perhatian dan kerja sama Bapak/Ibu, kami ucapkan terima kasih.")
                .append("</div>");

        emailContent.append("<div style=\"height:16px;line-height:16px;mso-line-height-rule:exactly;font-size:0;\">&nbsp;</div>");

        emailContent.append("<div style=\"margin:0;\">Credit Life Billing and Collection<br>PT MSIG Sharia Life Insurance Indonesia</div>");

        // Close inner table
        emailContent.append("            </td>");
        emailContent.append("          </tr>");
        emailContent.append("        </table>");
        emailContent.append("      </td>");
        emailContent.append("    </tr>");
        emailContent.append("  </table>");

        emailContent.append("</body>");
        emailContent.append("</html>");

        return emailContent.toString();
    }

    public List<SuratAutoCancelDTO> getListSuratPembatalanSertifikat(Integer reminderType) {
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

package id.co.msiglife.pollux.schduler.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import id.co.msiglife.pollux.schduler.dto.ApiResponseDTO;
import id.co.msiglife.pollux.schduler.dto.SuratAutoCancelDTO;
import id.co.msiglife.pollux.schduler.entity.MasterEmail;
import id.co.msiglife.pollux.schduler.repository.MasterEmailRepository;
import id.co.msiglife.pollux.schduler.repository.MasterPolicyRepository;
import id.co.msiglife.pollux.schduler.util.ConvertUtil;
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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static id.co.msiglife.pollux.schduler.util.ConvertUtil.*;

@Service
public class AutoCancelSuratKonfirmasiAwalService {

    @Value("${spring.custom.mailpool-attachment.path}")
    private String ATTACHMENT_PATH;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    MasterEmailRepository masterEmailRepository;

    @Autowired
    MasterPolicyRepository masterPolicyRepository;

    @Autowired
    private ConvertUtil convertUtil;

    @Autowired
    private FileProcessUtil fileProcessUtil;

//    @Scheduled(cron = "0 0 22 * * ?")
    public ApiResponseDTO<Map<String, Object>> sendSuratKonfirmasiEmail() {
        Map<String, Object> result = new HashMap<>();

        try {
            String sql =
                    "SELECT " +
                            "pol.mspo_policy_no AS \"Nomor Sertfikat\", " +
                            "bankCabangClient.mcl_first AS \"Nama Bank Cabang\", " +
                            "tertanggungClient.mcl_first AS \"Nama Tertanggung\", " +
                            "tertanggungPerson.mspe_date_birth AS \"Tanggal Lahir Tertanggung\", " +
                            "insured.mste_age AS \"Usia Tertanggung\", " +
                            "pol.mspo_ins_period AS \"Masa Asuransi Tahun\", " +
                            "pol.mspo_ins_bulan AS \"Masa Asuransi Bulan\", " +
                            "uw.premium AS \"Premi\", " +
                            "pol.mspo_date_print AS \"Tanggal Cetak Sertifikat\", " +
                            "bankPks.mcl_first AS \"Nama Bank PKS\", " +
                            "pks.atas_nama AS \"Nama Rekening PKS\", " +
                            "pks.rekening AS \"Nomor Rekening PKS\", " +
                            "bankCabangBranch.email_cabang AS \"Email Cabang\", " +
                            "bankCabangBranch.email_ao AS \"Email AO\", " +
                            "pol.reg_spaj AS \"Reg SPAJ\" " +
                            "FROM eka.mst_policy pol " +
                            "JOIN eka.mst_underwriting_plx uw ON pol.reg_spaj = uw.reg_spaj " +
                            "JOIN eka.mst_billing bill ON bill.reg_spaj = pol.reg_spaj " +
                            "JOIN eka.mst_pks_cl pks ON pol.pks_id = pks.pks_id " +
                            "JOIN eka.mst_insured insured ON pol.reg_spaj = insured.reg_spaj " +
                            "JOIN eka.mst_client tertanggungClient ON insured.mste_insured = tertanggungClient.mcl_id " +
                            "JOIN eka.mst_person tertanggungPerson ON insured.mste_insured = tertanggungPerson.mcl_id " +
                            "JOIN eka.mst_client bankCabangClient ON pol.mspo_customer = bankCabangClient.mcl_id " +
                            "JOIN eka.mst_branchpartner_cl bankCabangBranch ON pol.mspo_customer = bankCabangBranch.mcl_id " +
                            "JOIN eka.mst_client bankPks ON pks.mcl_id = bankPks.mcl_id " +
                            "WHERE TRUNC(SYSDATE) = TRUNC(pol.mspo_date_print + 7) " +
                            "AND EXISTS (SELECT 1 FROM eka.mst_billing bill WHERE bill.reg_spaj = pol.reg_spaj) " +
                            "AND NOT EXISTS (SELECT 1 FROM eka.mst_payment pay WHERE pay.reg_spaj = pol.reg_spaj) " +
                            "ORDER BY pol.mspo_date_print DESC";


            List<Object[]> results = entityManager.createNativeQuery(sql).getResultList();

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
                    (String) row[14]
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

            for (SuratAutoCancelDTO dto : dtoList) {

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

                // create string HTML to a JSoup Document to render XHTML
                Document document = Jsoup.parse(content);
                document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);

                // check if the folder path for attachments hasn't existed
                // (ex: //storage.sinarmasmsiglife.co.id/mailpools/attachments/2023/09/20230923661730)
                String attachmentFolderPath = ATTACHMENT_PATH + generatedId.substring(0, 4) + "/" + generatedId.substring(4, 6) + "/" + generatedId;

                // create the folder if it doesn't exist
                File folder = new File(attachmentFolderPath);
                File parentDirectory = folder.getParentFile();
                if (parentDirectory.canWrite()) {
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                } else {
                    throw new IOException("No write permissions in the parent directory: " + parentDirectory);
                }

                // PdfRendererBuilder will take this XHTML document and create a PDF as the output file
                String outputPdf = attachmentFolderPath + "/" + dto.getRegSpaj() + "_SURAT_KONFIRMASI_AWAL.pdf";

                try (OutputStream os = Files.newOutputStream(Paths.get(outputPdf))) {
                    PdfRendererBuilder builder = new PdfRendererBuilder();
                    builder.withUri(outputPdf);
                    builder.toStream(os);
                    builder.withW3cDocument(new W3CDom().fromJsoup(document), attachmentFolderPath + "/");
                    builder.run();
                }

//                // get list of kps and riplay, then sort by latest to oldest created date
//                List<MasterUploadDokumen> uploadKPSDokumenList = masterUploadDokumenRepository.findByRegSpajAndJenisDokumenId(regSpaj, 14); // find kps docs
//                List<MasterUploadDokumen> uploadRiplayDokumenList = masterUploadDokumenRepository.findByRegSpajAndJenisDokumenId(regSpaj, 13); // find riplay docs
//
//                uploadKPSDokumenList.sort(Comparator.comparing(MasterUploadDokumen::getUploadCreatedDate).reversed());
//                uploadRiplayDokumenList.sort(Comparator.comparing(MasterUploadDokumen::getUploadCreatedDate).reversed());
//
//                // trim filename based on the latest kps and riplay document
//                System.out.println("KPS: " + uploadKPSDokumenList.get(0).getPathFile());
//                System.out.println("Riplay: " + uploadRiplayDokumenList.get(0).getPathFile());
//                String kpsFilename = uploadKPSDokumenList.get(0).getPathFile().substring(uploadKPSDokumenList.get(0).getPathFile().indexOf("_KPS"));
//                String riplayFilename = uploadRiplayDokumenList.get(0).getPathFile().substring(uploadRiplayDokumenList.get(0).getPathFile().indexOf("_RIPLAY"));
//
//                // Source KPS and Riplay file path
//                String kpsSourceFilePath = STORAGE_PATH + regSpaj + "/" + regSpaj + kpsFilename;
//                System.out.println("Printed KPS source file path: " + kpsSourceFilePath);
//                Path kpsSourcePath = Paths.get(kpsSourceFilePath).toAbsolutePath();
//
//                String riplaySourceFilePath = STORAGE_PATH + regSpaj + "/" + regSpaj + riplayFilename;
//                System.out.println("Printed Riplay source file path: " + riplaySourceFilePath);
//                Path riplaySourcePath = Paths.get(riplaySourceFilePath).toAbsolutePath();
//
//                // Validate source of both file
//                if (!Files.exists(kpsSourcePath) || !Files.isReadable(kpsSourcePath)) {
//                    throw new IOException("KPS source file does not exist or is not readable: " + kpsSourceFilePath);
//                }
//
//                if (!Files.exists(riplaySourcePath) || !Files.isReadable(riplaySourcePath)) {
//                    throw new IOException("Riplay source file does not exist or is not readable: " + riplaySourcePath);
//                }
//
//                // Destination file path
//                String kpsDestinationFilePath = attachmentFolderPath + "/" + regSpaj + kpsFilename;
//                System.out.println("Printed KPS destination file path: " + kpsDestinationFilePath);
//                Path kpsDestinationPath = Paths.get(kpsDestinationFilePath).toAbsolutePath();
//
//                String riplayDestinationFilePath = attachmentFolderPath + "/" + regSpaj + riplayFilename;
//                System.out.println("Printed Riplay destination file path: " + riplayDestinationFilePath);
//                Path riplayDestinationPath = Paths.get(riplayDestinationFilePath).toAbsolutePath();
//
//                // Copy the file
//                Files.copy(kpsSourcePath, kpsDestinationPath, StandardCopyOption.REPLACE_EXISTING);
//                Files.copy(riplaySourcePath, riplayDestinationPath, StandardCopyOption.REPLACE_EXISTING);
//
//                // Check if the copies of both files are readable
//                if (!Files.isReadable(kpsDestinationPath)) {
//                    throw new IOException("The copied KPS file is not readable: " + kpsDestinationFilePath);
//                }
//
//                if (!Files.isReadable(riplayDestinationPath)) {
//                    throw new IOException("The copied Riplay file is not readable: " + riplayDestinationPath);
//                }
//
//                System.out.println("Both files are copied successfully: KPS: " + kpsDestinationFilePath + " Riplay: " + riplayDestinationPath);

                // set emailTo, subject, message, etc.
                String emailTo = stringBlank(dto.getEmailAo()) ? "" : dto.getEmailAo() + ";" + (stringBlank(dto.getEmailCabang()) ? "" : dto.getEmailCabang());

                String subject = "Surat Konfirmasi Pembayaran Premi a/n " + dto.getNamaTertanggung() + " No Sertifikat " + dto.getNomorSertfikat();
                String message = templateSuratKonfirmasiEmail(dto.getNamaBankCabang(), dto.getNamaTertanggung(), dto.getNomorSertfikat(), convertDateToJakartaTime(dto.getTanggalCetakSertifikat()));

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
                emailEntity.setPathAttach(attachmentFolderPath);

                masterEmailRepository.save(emailEntity);
                System.out.println("KPS and Riplay Email Sent Successfully...");
            }


            result.put("dto", dtoList);

            return new ApiResponseDTO<>(200, "Email sent successfully.", result);

        }
//        catch (IOException e) {
//            System.err.println("An error occurred during the file operation: " + e.getMessage());
//            result.put("error", "An error occurred during the file operation: " + e.getMessage());
//            e.printStackTrace();
//            return new ApiResponseDTO<>(500, "Internal Server Error", result);
//        }
        catch (IllegalArgumentException | IllegalStateException e) {
            result.put("error", e.getMessage());
            return new ApiResponseDTO<>(400, "Bad request", result);
        } catch (Exception e) {
            System.out.println("Error log: ");
            result.put("error", "Failed to save data. " + e.getMessage());
            e.printStackTrace();
            return new ApiResponseDTO<>(500, "Internal Server Error", result);
        }
    }

    private String templateSuratKonfirmasiEmail(String namaCabang, String namaTertanggung, String noSertifikat, String tanggalTerbitSertifikat) {
        return "<!DOCTYPE html>\n"
                + "<html lang=\"id\">\n"
                + "<head>\n"
                + "    <meta charset=\"UTF-8\">\n"
                + "    <title>Surat Konfirmasi Pembayaran Premi</title>\n"
                + "</head>\n"
                + "<body>\n"
                + "\n"
                + "    <p>Kepada Yth.<br>\n"
                + "    <strong>" + namaCabang + "</strong></p>\n"
                + "\n"
                + "    <p>Kami mengucapkan terima kasih kepada Bapak/Ibu yang telah mempercayakan kami, PT MSIG Life Insurance Indonesia Tbk. untuk memberikan perlindungan Asuransi Jiwa Kredit kepada Bapak/Ibu.</p>\n"
                + "\n"
                + "    <p>Berikut kami sampaikan <strong>Surat Konfirmasi Pembayaran Premi</strong> untuk sertifikat Asuransi Jiwa Kredit atas nama " + namaTertanggung + " dengan nomor sertifikat " + noSertifikat + " yang telah diterbitkan pada tanggal " + tanggalTerbitSertifikat + ".</p>\n"
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

}

package id.co.msiglife.pollux.schduler.service;

import id.co.msiglife.pollux.schduler.entity.MasterEmail;
import id.co.msiglife.pollux.schduler.entity.MasterPolicy;
import id.co.msiglife.pollux.schduler.repository.MasterEmailRepository;
import id.co.msiglife.pollux.schduler.repository.MasterPolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AutoCancelSuratKonfirmasiAwalService {
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ISO_DATE_TIME;

    @Autowired
    MasterEmailRepository masterEmailRepository;

    @Autowired
    MasterPolicyRepository masterPolicyRepository;

    @Scheduled(cron = "0 0 22 * * ?")
    public void testingCronWfLogActivity() {
        Map<String, Object> result = new HashMap<>();

        try {
            Integer userId = jwtUtil.getUserIdFromJwtToken(token);

            MasterPolicy masterPolicy = masterPolicyRepository.findByRegSpaj(regSpaj);
            MasterUnderwriting masterUnderwriting = masterUnderwritingRepository.findbyRegSpajk(regSpaj);
            MasterInsured masterInsured = masterInsuredRepository.findByRegSpajAndInsuredNo(regSpaj, 1);
            if (masterPolicy == null) {
                throw new RuntimeException("Policy not found");
            } else if (masterUnderwriting == null) {
                throw new RuntimeException("Underwriting not found");
            } else if (masterInsured == null) {
                throw new RuntimeException("Insured not found");
            }

            MasterClient masterClientPemegangPolisCabang = masterClientRepository.findByMclId(masterPolicy.getCustomer());
            MasterClient masterClientTertanggung = masterClientRepository.findByMclId(masterInsured.getInsured());
            MasterBranchPartner masterBranchPartner = masterBranchPartnerRepository.findByMclId(masterPolicy.getCustomer());
            if (masterClientPemegangPolisCabang == null) {
                throw new RuntimeException("Client Pemegang Polis Cabang not found");
            } else if (masterClientTertanggung == null) {
                throw new RuntimeException("Client Tertanggung not found");
            } else if (masterBranchPartner == null) {
                throw new RuntimeException("Branch Partner not found");
            }

            // Fetch the email ID from the SQL function using a native SQL query
            String generatedId = entityManager.createNativeQuery("SELECT EKA.PAC_COUNTER.GET_EMAIL_ID FROM DUAL").getSingleResult().toString();
            Date sysDate = (Date) entityManager.createNativeQuery("SELECT SYSDATE FROM DUAL").getSingleResult();
            System.out.println("Printed generated email ID: " + generatedId);

            // Define the folder path for attachments
            // (ex: //storage.sinarmasmsiglife.co.id/mailpools/attachments/2023/09/20230923661730)
            String attachmentFolderPath = ATTACHMENT_PATH + generatedId.substring(0, 4) + "/" + generatedId.substring(4, 6) + "/" + generatedId;
            System.out.println("Printed attachment folder: " + attachmentFolderPath);

            // Create the folder if it doesn't exist
            File folder = new File(attachmentFolderPath);
            if (!folder.exists()) {
                if (!folder.mkdirs()) {
                    throw new IOException("Failed to create directory: " + attachmentFolderPath);
                }
            }

            // get list of kps and riplay, then sort by latest to oldest created date
            List<MasterUploadDokumen> uploadKPSDokumenList = masterUploadDokumenRepository.findByRegSpajAndJenisDokumenId(regSpaj, 14); // find kps docs
            List<MasterUploadDokumen> uploadRiplayDokumenList = masterUploadDokumenRepository.findByRegSpajAndJenisDokumenId(regSpaj, 13); // find riplay docs

            uploadKPSDokumenList.sort(Comparator.comparing(MasterUploadDokumen::getUploadCreatedDate).reversed());
            uploadRiplayDokumenList.sort(Comparator.comparing(MasterUploadDokumen::getUploadCreatedDate).reversed());

            // trim filename based on the latest kps and riplay document
            System.out.println("KPS: " + uploadKPSDokumenList.get(0).getPathFile());
            System.out.println("Riplay: " + uploadRiplayDokumenList.get(0).getPathFile());
            String kpsFilename = uploadKPSDokumenList.get(0).getPathFile().substring(uploadKPSDokumenList.get(0).getPathFile().indexOf("_KPS"));
            String riplayFilename = uploadRiplayDokumenList.get(0).getPathFile().substring(uploadRiplayDokumenList.get(0).getPathFile().indexOf("_RIPLAY"));

            // Source KPS and Riplay file path
            String kpsSourceFilePath = STORAGE_PATH + regSpaj + "/" + regSpaj + kpsFilename;
            System.out.println("Printed KPS source file path: " + kpsSourceFilePath);
            Path kpsSourcePath = Paths.get(kpsSourceFilePath).toAbsolutePath();

            String riplaySourceFilePath = STORAGE_PATH + regSpaj + "/" + regSpaj + riplayFilename;
            System.out.println("Printed Riplay source file path: " + riplaySourceFilePath);
            Path riplaySourcePath = Paths.get(riplaySourceFilePath).toAbsolutePath();

            // Validate source of both file
            if (!Files.exists(kpsSourcePath) || !Files.isReadable(kpsSourcePath)) {
                throw new IOException("KPS source file does not exist or is not readable: " + kpsSourceFilePath);
            }

            if (!Files.exists(riplaySourcePath) || !Files.isReadable(riplaySourcePath)) {
                throw new IOException("Riplay source file does not exist or is not readable: " + riplaySourcePath);
            }

            // Destination file path
            String kpsDestinationFilePath = attachmentFolderPath + "/" + regSpaj + kpsFilename;
            System.out.println("Printed KPS destination file path: " + kpsDestinationFilePath);
            Path kpsDestinationPath = Paths.get(kpsDestinationFilePath).toAbsolutePath();

            String riplayDestinationFilePath = attachmentFolderPath + "/" + regSpaj + riplayFilename;
            System.out.println("Printed Riplay destination file path: " + riplayDestinationFilePath);
            Path riplayDestinationPath = Paths.get(riplayDestinationFilePath).toAbsolutePath();

            // Copy the file
            Files.copy(kpsSourcePath, kpsDestinationPath, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(riplaySourcePath, riplayDestinationPath, StandardCopyOption.REPLACE_EXISTING);

            // Check if the copies of both files are readable
            if (!Files.isReadable(kpsDestinationPath)) {
                throw new IOException("The copied KPS file is not readable: " + kpsDestinationFilePath);
            }

            if (!Files.isReadable(riplayDestinationPath)) {
                throw new IOException("The copied Riplay file is not readable: " + riplayDestinationPath);
            }

            System.out.println("Both files are copied successfully: KPS: " + kpsDestinationFilePath + " Riplay: " + riplayDestinationPath);

            // set emailTo, subject, message, etc.
            String emailTo = masterBranchPartner.getEmailAo() != null ? masterBranchPartner.getEmailAo() : "";

            String subject = "Surat Konfirmasi Pembayaran Premi a/n " + masterClientTertanggung.getFirst() + " No Sertifikat " + masterClientPemegangPolisCabang.getFirst();
            String message = templateSuratKonfirmasiEmail(regSpaj, masterClientTertanggung.getFirst(), masterClientPemegangPolisCabang.getFirst());

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
            emailEntity.setUserId(userId);  // Set the appropriate user ID
            emailEntity.setInputDate(sysDate);
            emailEntity.setSentDate(null); // Leave it as null or set to a specific date if scheduling
            emailEntity.setSchedulerDate(sysDate); // Set the specific date for scheduling or leave it as sysdate for immediate sending
            emailEntity.setPriority(50);
            emailEntity.setJenisId(50);
            emailEntity.setFlagNew(0);
            emailEntity.setRegSpaj(regSpaj);
            emailEntity.setPathAttach(attachmentFolderPath);

            masterEmailRepository.save(emailEntity);
            System.out.println("KPS and Riplay Email Sent Successfully...");

            result.put("email", emailEntity);

            return new ApiResponseDTO<>(200, "Email sent successfully.", result);

        } catch (IOException e) {
            System.err.println("An error occurred during the file operation: " + e.getMessage());
            result.put("error", "An error occurred during the file operation: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponseDTO<>(500, "Internal Server Error", result);
        } catch (IllegalArgumentException | IllegalStateException e) {
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

package id.co.msiglife.pollux.schduler.service.BcaRiplay;

import id.co.msiglife.pollux.schduler.dto.BcaRiplayDTO;
import id.co.msiglife.pollux.schduler.entity.MasterEmail;
import id.co.msiglife.pollux.schduler.repository.MasterEmailRepository;
import id.co.msiglife.pollux.schduler.repository.MasterPolicyRepository;
import id.co.msiglife.pollux.schduler.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BcaRiplayService {

    @Value("${spring.custom.storage.path}")
    private String STORAGE_PATH;

    @Value("${spring.custom.mailpool-attachment.path}")
    private String ATTACHMENT_PATH;

    @Value("${spring.custom.api-new-business-process-url}")
    private String API_NEW_BUSINESS_URL;


    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    MasterEmailRepository masterEmailRepository;

    @Autowired
    MasterPolicyRepository masterPolicyRepository;

    @Transactional
    public void processBcaRiplayList(List<BcaRiplayDTO> bcaRiplayList) throws Exception {

        Date sysDate = (Date) entityManager.createNativeQuery("SELECT SYSDATE FROM DUAL").getSingleResult();

        for (BcaRiplayDTO bcaRiplayDTO : bcaRiplayList) {

            Path sourcePath = bcaRiplayDTO.getPathFile() != null && !bcaRiplayDTO.getPathFile().isEmpty() ? Paths.get(bcaRiplayDTO.getPathFile()).toAbsolutePath() : null;

            String generatedId = entityManager.createNativeQuery("SELECT EKA.PAC_COUNTER.GET_EMAIL_ID FROM DUAL").getSingleResult().toString();
            String attachmentFolderPath = ATTACHMENT_PATH + generatedId.substring(0, 4) + "/" + generatedId.substring(4, 6) + "/" + generatedId;

            File folder = new File(attachmentFolderPath);
            if (!folder.exists()) {
                if (!folder.mkdirs()) {
                    System.err.println("Failed to create directory: " + attachmentFolderPath
                            + " | regSpaj: " + bcaRiplayDTO.getRegSpaj());
                    continue; // lanjut ke data berikutnya
                }
            }

            if (sourcePath != null && Files.exists(sourcePath) && Files.isReadable(sourcePath)) {
                try {

                    String destinationFilePath = attachmentFolderPath + "/" + sourcePath.getFileName();
                    Path destinationPath = Paths.get(destinationFilePath).toAbsolutePath();

                    try {
                        Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        System.err.println("Gagal copy file: " + bcaRiplayDTO.getRegSpaj());
                        continue;
                    }
                    if (!Files.isReadable(destinationPath)) {
                        System.err.println("Failed to generate PDF for regSpaj: " + bcaRiplayDTO.getRegSpaj());
                        continue;
                    }

                    String riplayBcaSubject = "[PENTING] Ringkasan Informasi Produk dan Layanan Personal Asuransi Jiwa Kredit - MSIG Life x Bank BCA";
                    String message = templateKpsRiplayEmailBca(bcaRiplayDTO.getNamaTertanggung());

                    MasterEmail emailEntity = new MasterEmail();
                    emailEntity.setId(generatedId);
                    emailEntity.setSystem("POLLUX");
                    emailEntity.setShowSystem(0);
                    emailEntity.setShowFooter(0);
                    emailEntity.setFrom("cosmos@msiglife.co.id");
                    emailEntity.setFromAlias("Notifikasi POLLUX");
                    emailEntity.setTo(bcaRiplayDTO.getEmailTertanggung());
                    emailEntity.setCc("underwritingmriproses@msiglife.co.id;collectionmri@msiglife.co.id");
                    emailEntity.setBcc(null);
                    emailEntity.setSubject(riplayBcaSubject);
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
                    emailEntity.setRegSpaj(bcaRiplayDTO.getRegSpaj());
                    emailEntity.setPathAttach(destinationPath.toString());

                    masterEmailRepository.save(emailEntity);
                } catch (IOException e) {
                    System.err.println("Failed to generate PDF for regSpaj: " + bcaRiplayDTO.getRegSpaj() + " | Error: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.err.println("File Attachment not exist trying get from storage MRI " + bcaRiplayDTO.getRegSpaj());
                int maxRetries = 3;
                int attempt = 0;

                while (attempt < maxRetries) {
                    HttpURLConnection connection = null;

                    String fileName = bcaRiplayDTO.getNamaCabang() + "/" + bcaRiplayDTO.getRegSpaj() + " RIPLAY PERSONAL.pdf";
                    String encodedUrl = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
                    try {
                        attempt++;

                        URL url = new URI(
                                API_NEW_BUSINESS_URL
                                        + "?reg_spaj=" + bcaRiplayDTO.getRegSpaj()
                                        + "&url=" + encodedUrl
                        ).toURL();

                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(15000);
                        connection.setReadTimeout(15000);
                        connection.setRequestProperty("Accept", "application/pdf");

                        int responseCode = connection.getResponseCode();

                        if (responseCode == HttpURLConnection.HTTP_OK) {

                            InputStream inputStream = connection.getInputStream();

                            Path destinationPath = Paths.get(
                                    attachmentFolderPath + "/" + bcaRiplayDTO.getRegSpaj() + " RIPLAY PERSONAL.pdf"
                            ).toAbsolutePath();

                            Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);

                            // validasi file
                            if (!Files.exists(destinationPath) || !Files.isReadable(destinationPath)) {
                                System.err.println("File gagal dibuat dari API: " + bcaRiplayDTO.getRegSpaj());
                                continue;
                            }

                            System.out.println("Success get file on attempt " + attempt + ": " + bcaRiplayDTO.getRegSpaj());
                            sourcePath = destinationPath;

                            String riplayBcaSubject = "[PENTING] Ringkasan Informasi Produk dan Layanan Personal Asuransi Jiwa Kredit - MSIG Life x Bank BCA";
                            String message = templateKpsRiplayEmailBca(bcaRiplayDTO.getNamaTertanggung());

                            MasterEmail emailEntity = new MasterEmail();
                            emailEntity.setId(generatedId);
                            emailEntity.setSystem("POLLUX");
                            emailEntity.setShowSystem(0);
                            emailEntity.setShowFooter(0);
                            emailEntity.setFrom("cosmos@msiglife.co.id");
                            emailEntity.setFromAlias("Notifikasi POLLUX");
                            emailEntity.setTo(bcaRiplayDTO.getEmailTertanggung());
                            emailEntity.setCc("underwritingmriproses@msiglife.co.id;collectionmri@msiglife.co.id");
                            emailEntity.setBcc(null);
                            emailEntity.setSubject(riplayBcaSubject);
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
                            emailEntity.setRegSpaj(bcaRiplayDTO.getRegSpaj());
                            emailEntity.setPathAttach(destinationPath.toString());

                            masterEmailRepository.save(emailEntity);

                            break;

                        } else {
                            System.out.println("Attempt " + attempt + " failed: HTTP " + responseCode);

                            InputStream errorStream = connection.getErrorStream();
                            if (errorStream != null) {
                                String error = new BufferedReader(new InputStreamReader(errorStream))
                                        .lines()
                                        .collect(Collectors.joining("\n"));
                                System.out.println("Error response: " + error);
                            }
                        }

                    } catch (Exception e) {
                        System.err.println("Attempt " + attempt + " error: " + e.getMessage());
                        e.printStackTrace();
                    } finally {
                        if (connection != null) {
                            connection.disconnect();
                        }
                    }
                }

                if (sourcePath == null || !Files.exists(sourcePath)) {
                    System.err.println("All retry attempts failed for: " + bcaRiplayDTO.getRegSpaj());
                    continue;
                }
        }
    }
}

private String templateKpsRiplayEmailBca(String namaTertanggung) throws Exception {
    StringBuilder sb = new StringBuilder();

    sb.append("Yth. Bapak/Ibu ")
            .append(namaTertanggung)
            .append(",\n\n");

    sb.append("Terima kasih atas kepercayaan Bapak/Ibu telah mengajukan Asuransi Jiwa Kredit melalui Bank BCA.\n\n");

    sb.append("Dengan ini kami informasikan bahwa pengajuan asuransi Bapak/Ibu telah disetujui. ");
    sb.append("Sebagai bagian dari transparansi layanan, bersama email ini kami lampirkan ");
    sb.append("Ringkasan Informasi Produk dan Layanan Personal Asuransi Jiwa Kredit. ");
    sb.append("Dokumen ini berisi informasi penting terkait manfaat, ketentuan, serta layanan ");
    sb.append("asuransi yang perlu Bapak/Ibu ketahui dan pahami.\n\n");

    sb.append("Mohon Bapak/Ibu dapat mempelajari dokumen terlampir dengan seksama. ");
    sb.append("Apabila terdapat pertanyaan atau membutuhkan penjelasan lebih lanjut, ");
    sb.append("Bapak/Ibu dapat menghubungi:\n\n");

    sb.append("    - Customer Care MSIG Life di (021) 5060 9999; (021) 2650 8300\n");
    sb.append("    - Atau melalui email ke groupcs@msiglife.co.id\n\n");

    sb.append("Demikian kami sampaikan. Semoga perlindungan asuransi ini dapat memberikan ");
    sb.append("rasa aman dan manfaat terbaik bagi Bapak/Ibu.\n\n");

    sb.append("Hormat kami,\n");
    sb.append("PT MSIG Life Insurance Indonesia Tbk");

    return sb.toString();
}

public List<BcaRiplayDTO> getBcaRiplayList() {
    List<Object[]> listData = masterPolicyRepository.findSendAllRiplayBca();
    List<BcaRiplayDTO> result = new ArrayList<>();

    for (Object[] row : listData) {
        BcaRiplayDTO dto = new BcaRiplayDTO();
        dto.setRegSpaj(ConvertUtil.safeString(row[0]));
        dto.setPolicyNo(ConvertUtil.safeString(row[1]));
        dto.setInputDate(ConvertUtil.safeDate(row[2]));
        dto.setGenerateRiplayDate(ConvertUtil.safeDate(row[3]));
        dto.setNamaTertanggung(ConvertUtil.safeString(row[4]));
        dto.setDob(ConvertUtil.safeDate(row[5]));
        dto.setLswfConfigAction(ConvertUtil.safeString(row[6]));
        dto.setEmailTertanggung(ConvertUtil.safeString(row[7]));
        dto.setNamaCabang(ConvertUtil.safeString(row[8]));
        dto.setPathFile(ConvertUtil.safeString(row[9]));
        result.add(dto);
    }

    return result;
}

}

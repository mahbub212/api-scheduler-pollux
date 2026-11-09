package id.co.msiglife.pollux.schduler.service.surrender;

import id.co.msiglife.pollux.schduler.dto.surrender.EmailApprovalSurrenderDTO;
import id.co.msiglife.pollux.schduler.dto.surrender.ListUWHistoryApprovalDTO;
import id.co.msiglife.pollux.schduler.entity.MasterEmail;
import id.co.msiglife.pollux.schduler.exception.BadRequestException;
import id.co.msiglife.pollux.schduler.exception.FileException;
import id.co.msiglife.pollux.schduler.exception.NotFoundException;
import id.co.msiglife.pollux.schduler.repository.MasterAutoCancelEmailLogRepository;
import id.co.msiglife.pollux.schduler.repository.MasterEmailRepository;
import id.co.msiglife.pollux.schduler.repository.MasterPolicyRepository;
import id.co.msiglife.pollux.schduler.util.ConvertUtil;
import id.co.msiglife.pollux.schduler.util.StaticValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.NumberFormat;
import java.util.*;

@Service
public class EmailApprovalSurrenderService {

    @Value("${spring.custom.mailpool-attachment.path}")
    private String ATTACHMENT_PATH;

    @Value("${spring.custom.storage.path}")
    private String STORAGE_PATH;

    @Value("${spring.custom.url.login-pollux}")
    private String URL_LOGIN_POLLUX;

    @Autowired
    MasterPolicyRepository masterPolicyRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MasterEmailRepository masterEmailRepository;

    @Autowired
    private MasterAutoCancelEmailLogRepository masterAutoCancelEmailLogRepository;

    @Transactional
    public void executeTask(List<EmailApprovalSurrenderDTO> cancelSpajkDTOList) {
        for (EmailApprovalSurrenderDTO dto : cancelSpajkDTOList) {
            try {
                String meId = sendRequestApprovalSurrender(dto);
                int updated = masterAutoCancelEmailLogRepository.updateEmailLogDone(dto.getRegSpaj(), StaticValue.EMAIL_APPROVAL_SURRENDER_TYPE, meId);
                if (updated == 1) {
                    System.out.println("Sending approval surrender for SPAJ Successfully");
                } else {
                    System.out.println("Error when updating approval surrender for SPAJ " + dto.getRegSpaj());
                }
            } catch (Exception e) {
                System.out.println(
                        "Failed send approval surrender for SPAJ "
                                + dto.getRegSpaj()
                                + " : "
                                + e.getMessage()
                );
                e.printStackTrace();
                continue;
            }
        }
    }

    @Transactional
    public String sendRequestApprovalSurrender(EmailApprovalSurrenderDTO emailApprovalSurrenderDTO) {

        // Fetch the email ID from the SQL function using a native SQL query
        String generatedId = entityManager.createNativeQuery("SELECT EKA.PAC_COUNTER.GET_EMAIL_ID FROM DUAL").getSingleResult().toString();
        if (generatedId == null || generatedId.isEmpty() || ConvertUtil.stringBlank(generatedId)) {
            throw new BadRequestException("Failed to generate email ID from PAC_COUNTER.GET_EMAIL_ID");
        }
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
                throw new FileException("Failed to create directory: " + attachmentFolderPath);
            }
        }

        // Source file path
        String sourceFilePath = STORAGE_PATH + emailApprovalSurrenderDTO.getRegSpaj() + "/" + StaticValue.namingFileNT.replace("{{noPolis}}", emailApprovalSurrenderDTO.getNoPolis()) + ".pdf";
        System.out.println("Printed source file path: " + sourceFilePath);
        Path sourcePath = Paths.get(sourceFilePath).toAbsolutePath();

        // Validate source file
        if (!Files.exists(sourcePath) || !Files.isReadable(sourcePath)) {
            throw new FileException("Source file does not exist or is not readable: " + sourceFilePath);
        }

        // Destination file path
        String destinationFilePath = attachmentFolderPath + "/" + StaticValue.namingFileNT.replace("{{noPolis}}", emailApprovalSurrenderDTO.getNoPolis()) + ".pdf";
        System.out.println("Printed destination file path: " + destinationFilePath);
        Path destinationPath = Paths.get(destinationFilePath).toAbsolutePath();

        // Copy the file
        try {
            Files.copy(
                    sourcePath,
                    destinationPath,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed copy attachment for SPAJ " + emailApprovalSurrenderDTO.getRegSpaj(),
                    e
            );
        }

        // Check if the copied file is readable
        if (!Files.isReadable(destinationPath)) {
            throw new FileException("The copied file is not readable: " + destinationFilePath);
        }

        System.out.println("File copied successfully to: " + destinationFilePath);

        List<Object[]> rawData = masterPolicyRepository.findEmailAndFullName(emailApprovalSurrenderDTO.getRegSpaj(), emailApprovalSurrenderDTO.getFlowTypeId());

        List<String> emailList = new ArrayList<>();
        List<String> fullNameList = new ArrayList<>();

        for (Object[] row : rawData) {
            if (row[0] != null && row[1] != null) { // Ensure no null values
                emailList.add(row[0].toString());  // LUS_EMAIL
                fullNameList.add(row[1].toString()); // LUS_FULL_NAME
            }
        }

        if (emailList.isEmpty()) {
            throw new NotFoundException("No valid emails found for the specified query");
        }

        String emailTo = String.join(";", emailList);
        String nameTo = String.join(";", fullNameList);

        String subject = "Request Approval Surrender {nama_tertanggung} No Polis {no_polis} ";

        subject = subject.replace("{nama_tertanggung}", emailApprovalSurrenderDTO.getInsuredName());
        subject = subject.replace("{no_polis}", emailApprovalSurrenderDTO.getNoPolis());

        String message = templateApprovalSurrenderEmail(emailApprovalSurrenderDTO, nameTo);

        if (message == null || ConvertUtil.stringBlank(message)) {
            throw new BadRequestException("Email message template returned empty content");
        }

        MasterEmail emailEntity = new MasterEmail();
        emailEntity.setId(generatedId);
        emailEntity.setSystem("POLLUX");
        emailEntity.setShowSystem(0);
        emailEntity.setShowFooter(0);
        emailEntity.setFrom("cosmos@msiglife.co.id");
        emailEntity.setFromAlias("Notifikasi POLLUX");
        emailEntity.setTo(emailTo);
        emailEntity.setCc("pasmri@msiglife.co.id");
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
        emailEntity.setPriority(99);
        emailEntity.setJenisId(50);
        emailEntity.setFlagNew(0);
        emailEntity.setRegSpaj(emailApprovalSurrenderDTO.getRegSpaj());
        emailEntity.setPathAttach(destinationPath.toString());

        masterEmailRepository.save(emailEntity);
        System.out.println("Update Data Approval Refund Sent Successfully...");

        return generatedId;
    }

    private String templateApprovalSurrenderEmail(EmailApprovalSurrenderDTO emailApprovalSurrenderDTO, String nameTo) {
        List<Object[]> result = masterPolicyRepository.findByRegSpaj(emailApprovalSurrenderDTO.getRegSpaj(), false, emailApprovalSurrenderDTO.getFlowTypeId());

        if (!result.isEmpty()) {
            Object[] object = result.get(0); // ambil record yang isApproved-nya = 0, role yang setelahnya akan nge-approve

            ListUWHistoryApprovalDTO dto = new ListUWHistoryApprovalDTO();
            dto.setApprovalHistId(((BigDecimal) object[0]).longValue());
            dto.setRegSpaj((String) object[1]);
            dto.setMsuwId(object[2] != null ? ((BigDecimal) object[2]).longValue() : null);
            dto.setRoleId(((BigDecimal) object[3]).intValue());
            dto.setIsApproved(object[4].equals(BigDecimal.ONE));
            dto.setDecision(!ConvertUtil.stringBlank((String) object[5]) ? (String) object[5] : "");
            dto.setLusId(object[6] != null ? ((BigDecimal) object[6]).intValue() : null);
            dto.setRemark(ConvertUtil.clobToString(object[7]) != null ? ConvertUtil.clobToString(object[7]) : "");
            dto.setCreatedDate((Date) object[8]);
            dto.setApprovedDate(object[9] != null ? (Date) object[9] : null);
            dto.setUserName(!ConvertUtil.stringBlank((String) object[10]) ? (String) object[10] : "");
            dto.setRoleName(!ConvertUtil.stringBlank((String) object[11]) ? (String) object[11] : "");
            dto.setPemegangPolis(!ConvertUtil.stringBlank((String) object[12]) ? (String) object[12] : "");
            dto.setNamaTertanggung(!ConvertUtil.stringBlank((String) object[13]) ? (String) object[13] : "");
            dto.setProduct(!ConvertUtil.stringBlank((String) object[14]) ? (String) object[14] : "");
            dto.setUangPertanggungan(object[15] != null ? ((BigDecimal) object[15]) : null);
            dto.setKeputusan(!ConvertUtil.stringBlank((String) object[16]) ? (String) object[16] : "");
            dto.setFinalRemark(ConvertUtil.clobToString(object[17]) != null ? ConvertUtil.clobToString(object[17]) : "");
            dto.setTotalRefund(object[18] != null ? ((BigDecimal) object[18]) : null);
            dto.setReasonOfRefund(!ConvertUtil.stringBlank((String) object[19]) ? (String) object[19] : "");
            dto.setNoPolicy(!ConvertUtil.stringBlank((String) object[20]) ? (String) object[20] : "");
            dto.setReasonSurrender(!ConvertUtil.stringBlank((String) object[21]) ? (String) object[21] : "");
            dto.setNamaCabang(!ConvertUtil.stringBlank((String) object[22]) ? (String) object[22] : "");


            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

            return "Dear Bapak/Ibu " + nameTo + ". \n \n" +
                    "Mohon Approval atas pengajuan Surrender berikut ini:\n \n" +
                    "NO SPAJK \t\t: " + dto.getRegSpaj() + "\n" +
                    "NO POLIS \t\t: " + dto.getNoPolicy() + "\n" +
                    "NAMA PEMEGANG \t\t: " + dto.getPemegangPolis() + "\n" +
                    "NAMA CABANG \t\t: " + dto.getNamaCabang() + "\n" +
                    "NAMA TERTANGGUNG \t: " + dto.getNamaTertanggung() + "\n" +
                    "PRODUK \t\t\t: " + (emailApprovalSurrenderDTO.getIsPolluxRate() == 1 ? dto.getProduct() : "-") + "\n" +
                    "UANG PERTANGGUNGAN \t\t : " + ConvertUtil.formattedOutputCurrency(dto.getUangPertanggungan()) + "\n" +
                    "PREMI \t\t : " + ConvertUtil.formattedOutputCurrency(emailApprovalSurrenderDTO.getPremi().add(emailApprovalSurrenderDTO.getExtraPremi())) + "\n" +
                    "NILAI TUNAI \t\t: " + ConvertUtil.formattedOutputCurrency(emailApprovalSurrenderDTO.getNilaiNt()) + "\n" +
                    "PAS NOTE \t\t: " + dto.getReasonSurrender() + "\n \n" +
                    "LOGIN FOR APPROVAL -> " + URL_LOGIN_POLLUX + "\n \n";
        }

        return "";
    }

    public List<EmailApprovalSurrenderDTO> getSpajkWillBeSendingApproval() {
        List<Object[]> resultList = masterPolicyRepository.findSpajkWillBeSendingApproval();
        List<EmailApprovalSurrenderDTO> spajkSendingEmailList = new ArrayList<>();
        for (Object[] row : resultList) {
            EmailApprovalSurrenderDTO dto = new EmailApprovalSurrenderDTO();
            dto.setRegSpaj(ConvertUtil.safeString(row[0]));
            dto.setPatchAttach(ConvertUtil.safeString(row[1]));
            dto.setMeId(ConvertUtil.safeString(row[2]));
            dto.setNoPolis(ConvertUtil.safeString(row[3]));
            dto.setFlowTypeId(ConvertUtil.safeInt(row[4]));
            dto.setInsuredName(ConvertUtil.safeString(row[5]));
            dto.setIsPolluxRate(ConvertUtil.safeInt(row[6]));
            dto.setPremi(ConvertUtil.safeBigDecimal(row[7]));
            dto.setExtraPremi(ConvertUtil.safeBigDecimal(row[8]));
            dto.setNilaiNt(ConvertUtil.safeBigDecimal(row[9]));
            dto.setLastApprove(ConvertUtil.safeInt(row[10]));
            spajkSendingEmailList.add(dto);
        }
        return spajkSendingEmailList;
    }
}

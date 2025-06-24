package id.co.msiglife.pollux.schduler.service;

import id.co.msiglife.pollux.schduler.dto.ApiResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

@Service
public class SurrenderService {

    @Value("${spring.custom.mailpool-attachment.path}")
    private String ATTACHMENT_PATH;

    @PersistenceContext
    private EntityManager entityManager;

    public ApiResponseDTO<Map<String, Object>> sendKonfirmasiPolisDibatalkanEmail(String regSpaj, String token) throws Exception {

        //TODO: finish service for sending Surat Konfirmasi Polis akan Dibatalkan

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

        return null;
    }
}

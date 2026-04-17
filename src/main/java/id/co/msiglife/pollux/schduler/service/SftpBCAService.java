package id.co.msiglife.pollux.schduler.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class SftpBCAService {

    @Value("${spring.custom.api-sftp-bca-url}")
    private String API_SEND_SFTP_BCA;

    @Scheduled(cron = "0 */1 * * * ?")
    public void hitSFTPBCA() {
        HttpURLConnection connection = null;

        System.out.println("Sending SFTP BCA");

        try {
            URL url = new URL(API_SEND_SFTP_BCA);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // Optional headers
            connection.setRequestProperty("Accept", "application/json");

            System.out.println("Done Send SFTP BCA");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
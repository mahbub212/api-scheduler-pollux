package id.co.msiglife.pollux.schduler.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class MasterPolicyService {
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ISO_DATE_TIME;

    @Scheduled(cron = "0 */5 * * * ?")
    public void cronSomething() {
        LocalDateTime now = LocalDateTime.now();

        System.out.println("check scheduler: " + now.format(dateFormat));
    }
}

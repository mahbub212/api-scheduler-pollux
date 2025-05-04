package id.co.msiglife.pollux.schduler.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.msiglife.pollux.schduler.entity.MasterWfLogActivityPollux;
import id.co.msiglife.pollux.schduler.repository.MasterWFDocumentRepository;
import id.co.msiglife.pollux.schduler.repository.MasterWfLogActivityPolluxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MasterPolicyScheduleService {
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ISO_DATE_TIME;

    @Autowired
    MasterWFDocumentRepository masterWFDocumentRepository;

    @Autowired
    MasterWfLogActivityPolluxRepository masterWfLogActivityPolluxRepository;

    @Scheduled(cron = "0 0 17 * * ?")
    public void testingCronWfLogActivity() {
        // I want to add current time + 30 days, generate the code please
        LocalDateTime now = LocalDateTime.now().minusDays(30);

        List<String> statusIds = Arrays.asList(new String[]{"3", "4"});
        String isoString = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        Map<String, Object> body = new HashMap<>();
        body.put("time", isoString);
        body.put("statusIds",statusIds);

        MasterWfLogActivityPollux testScheduleLogger = new MasterWfLogActivityPollux();
        testScheduleLogger.setIpAddress("127.255.255.254");
        testScheduleLogger.setEndpointUrl("/scheduler-engine");
        testScheduleLogger.setMethod("GET");

        System.out.println("check scheduler: " + now.format(dateFormat));
        try {
            System.out.println("where clause: " + "{ statusIds: "+ statusIds + ", lastUpdatedAt: " + isoString + "}");
            testScheduleLogger.setRequestBody(new ObjectMapper().writeValueAsString(body));
            testScheduleLogger.setResponse("Success running scheduler");
            testScheduleLogger.setSuccess(true);
        } catch (Exception e) {
            testScheduleLogger.setResponse("Failed to running scheduler");
            testScheduleLogger.setSuccess(false);
        } finally {
            testScheduleLogger.setUserId(1);
            masterWfLogActivityPolluxRepository.save(testScheduleLogger);
            System.out.println("result schedule logger: " + testScheduleLogger.getRequestBody());
        }
    }
}

package id.co.msiglife.pollux.schduler.job;

import id.co.msiglife.pollux.schduler.dto.CancelPolisDTO;
import id.co.msiglife.pollux.schduler.service.cancelPolis.CancelPolisService;
import id.co.msiglife.pollux.schduler.service.CheckLineBusiness.CheckLineBusinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class CancelPolisScheduler {

    @Autowired
    private CancelPolisService cancelPolisService;

    @Autowired
    private CheckLineBusinessService checkLineBusinessService;

    @Scheduled(cron = "0 */1 * * * ?")
    public void cancelPolisScheduler() {
        List<CancelPolisDTO> cancelPolisDTOList = cancelPolisService.getCancelPolisList();
        //Checking line business
        cancelPolisDTOList = checkLineBusinessService.checkLineBusinessPolis(cancelPolisDTOList);
        System.out.println("=================== START EXECUTING TASK: CANCEL POLIS (" + cancelPolisDTOList.size() + ") Data ===================");
        cancelPolisService.processCancelPolisList(cancelPolisDTOList);
        System.out.println("=================== FINISHED EXECUTING TASK: CANCEL POLIS ===================");
    }

    //@Scheduled(cron = "0 0 6 * * ?")
    public void retryCancelPolisScheduler() {
        List<CancelPolisDTO> cancelPolisDTOList = cancelPolisService.getCancelPolisList();
        //Checking line business
        cancelPolisDTOList = checkLineBusinessService.checkLineBusinessPolis(cancelPolisDTOList);
        System.out.println("=================== RETRY EXECUTING TASK: CANCEL POLIS (" + cancelPolisDTOList.size() + ") Data ===================");
        cancelPolisService.processCancelPolisList(cancelPolisDTOList);
        System.out.println("=================== FINISHED RETRYING EXECUTING TASK: CANCEL POLIS ===================");
    }

}

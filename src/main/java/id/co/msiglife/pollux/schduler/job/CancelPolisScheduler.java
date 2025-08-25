package id.co.msiglife.pollux.schduler.job;

import id.co.msiglife.pollux.schduler.dto.CancelPolisDTO;
import id.co.msiglife.pollux.schduler.service.CancelPolisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CancelPolisScheduler {

    @Autowired
    private CancelPolisService cancelPolisService;

    @Scheduled(cron = "0 40 1 * * ?")
    public void cancelSpajkScheduler(){
        List<CancelPolisDTO> cancelPolisDTOList = cancelPolisService.getCancelPolisList();
        System.out.println("=================== START EXECUTING TASK: CANCEL SPAJK (" + cancelPolisDTOList.size() + ") Data ===================");
        cancelPolisService.processCancelPolisList(cancelPolisDTOList);
        System.out.println("=================== FINISHED EXECUTING TASK: CANCEL SPAJK ===================");
    }

    @Scheduled(cron = "0 40 5 * * ?")
    public void retryCancelSpajkScheduler(){
        List<CancelPolisDTO> cancelPolisDTOList = cancelPolisService.getCancelPolisList();
        System.out.println("=================== RETRY EXECUTING TASK: CANCEL SPAJK (" + cancelPolisDTOList.size() + ") Data ===================");
        cancelPolisService.processCancelPolisList(cancelPolisDTOList);
        System.out.println("=================== FINISHED RETRYING EXECUTING TASK: CANCEL SPAJK ===================");
    }

}

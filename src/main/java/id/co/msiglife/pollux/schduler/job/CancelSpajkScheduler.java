package id.co.msiglife.pollux.schduler.job;

import id.co.msiglife.pollux.schduler.dto.CancelSpajkDTO;
import id.co.msiglife.pollux.schduler.service.CancelSpajkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CancelSpajkScheduler {

    @Autowired
    private CancelSpajkService cancelSpajkService;

    @Scheduled(cron = "0 40 1 * * ?")
    public void cancelSpajkScheduler(){
        List<CancelSpajkDTO> cancelSpajkDTOList = cancelSpajkService.getCancelSpajkList();
        System.out.println("=================== START EXECUTING TASK: CANCEL SPAJK (" + cancelSpajkDTOList.size() + ") Data ===================");
        cancelSpajkService.executeTask(cancelSpajkDTOList);
        System.out.println("=================== FINISHED EXECUTING TASK: CANCEL SPAJK ===================");
    }

    @Scheduled(cron = "0 40 5 * * ?")
    public void retryCancelSpajkScheduler(){
        List<CancelSpajkDTO> cancelSpajkDTOList = cancelSpajkService.getCancelSpajkList();
        System.out.println("=================== RETRY EXECUTING TASK: CANCEL SPAJK (" + cancelSpajkDTOList.size() + ") Data ===================");
        cancelSpajkService.executeTask(cancelSpajkDTOList);
        System.out.println("=================== FINISHED RETRYING EXECUTING TASK: CANCEL SPAJK ===================");
    }
}

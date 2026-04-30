package id.co.msiglife.pollux.schduler.job;

import id.co.msiglife.pollux.schduler.dto.BcaRiplayDTO;
import id.co.msiglife.pollux.schduler.service.BcaRiplay.BatchingBcaRiplayService;
import id.co.msiglife.pollux.schduler.service.BcaRiplay.BcaRiplayService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class BcaRiplayScheduler {

    @Autowired
    BcaRiplayService bcaRiplayService;

    @Autowired
    BatchingBcaRiplayService batchingBcaRiplayService;

    //@Scheduled(cron = "0 20 2 * * ?")
    public void bcaRiplayScheduler() throws Exception {
        System.out.println("=================== START FETCHING TASK: RIPLAY BCA ===================");
        List<BcaRiplayDTO> bcaRiplayList = bcaRiplayService.getBcaRiplayList(); //new ArrayList<>();
        System.out.println("=================== FINISHED FETCHING TASK: RIPLAY BCA ===================");
        System.out.println("=================== START EXECUTING TASK: RIPLAY BCA (" + bcaRiplayList.size() + ") Data ===================");
        batchingBcaRiplayService.processInBatch(bcaRiplayList);
        System.out.println("=================== FINISHED EXECUTING TASK: RIPLAY BCA ===================");
    }
}

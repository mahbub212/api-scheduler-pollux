package id.co.msiglife.pollux.schduler.job.surrender;

import id.co.msiglife.pollux.schduler.dto.CancelSpajkDTO;
import id.co.msiglife.pollux.schduler.dto.surrender.EmailApprovalSurrenderDTO;
import id.co.msiglife.pollux.schduler.service.surrender.EmailApprovalSurrenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class EmailApprovalSurrenderScheduler {

    private final EmailApprovalSurrenderService emailApprovalSurrenderService;
    //run every 4.40 P.M
    @Scheduled(cron = "0 40 16 * * ?")
    public void surrenderApprovalScheduler(){
        List<EmailApprovalSurrenderDTO> spajkSendingEmailList = emailApprovalSurrenderService.getSpajkWillBeSendingApproval();
        System.out.println("=================== START EXECUTING TASK: CANCEL SPAJK (" + spajkSendingEmailList.size() + ") Data ===================");
        emailApprovalSurrenderService.executeTask(spajkSendingEmailList);
        System.out.println("=================== FINISHED EXECUTING TASK: CANCEL SPAJK ===================");
    }

}

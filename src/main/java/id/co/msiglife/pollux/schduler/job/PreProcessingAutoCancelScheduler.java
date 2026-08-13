package id.co.msiglife.pollux.schduler.job;

import id.co.msiglife.pollux.schduler.dto.PreProcessingKonfirmasiSuratDTO;
import id.co.msiglife.pollux.schduler.service.CancelSpajkService;
import id.co.msiglife.pollux.schduler.service.PreProcessingAutoCancelService;
import id.co.msiglife.pollux.schduler.util.StaticValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class PreProcessingAutoCancelScheduler {

    @Autowired
    private PreProcessingAutoCancelService preProcessingAutoCancelService;

    @Scheduled(cron = "0 1 0 * * ?")
    public void preparationGenerateSuratKonfirmasi(){

        List<PreProcessingKonfirmasiSuratDTO> preProcessingDTOList = preProcessingAutoCancelService.generatePreProcessingSurat();
        System.out.println("=================== START INSERT PRE PROCESSING DATA (" + preProcessingDTOList.size() + ") Data ===================");
        preProcessingAutoCancelService.saveToLoggerAutoCancel(preProcessingDTOList);
        System.out.println("=================== END GENERATED PRE PROCESSING DATA ===================");

        List<PreProcessingKonfirmasiSuratDTO> preProcessingCancelList = preProcessingAutoCancelService.generatePreProcessingCancelSPAJK();
        System.out.println("=================== START INSERT PRE PROCESSING CANCELING DATA (" + preProcessingCancelList.size() + ") Data ===================");
        preProcessingAutoCancelService.saveToLoggerAutoCancelSPAJK(preProcessingCancelList);
        System.out.println("=================== END GENERATED PRE PROCESSING CANCELING DATA ===================");
    }
}

package id.co.msiglife.pollux.schduler.job;

import id.co.msiglife.pollux.schduler.entity.BcaData;
import id.co.msiglife.pollux.schduler.repository.MappingBcaRepository;
import id.co.msiglife.pollux.schduler.service.OrderBcaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderBcaScheduler {

    @Autowired
    private OrderBcaService orderBcaService;

    @Autowired
    private MappingBcaRepository mappingBcaRepository;

    @Scheduled(cron = "0 * * * * *")
    public void sendOrderBcaToAo() {
        System.out.println("=================== START SEND BCA Data to AO ===================");
        // get unsent order to ao
        List<BcaData> bcaDataList = mappingBcaRepository.findUnsentOrdersToAo();

        if (bcaDataList == null || bcaDataList.isEmpty()) {
            System.out.println("No BCA orders to send to AO");
        } else {
            System.out.println("Process: " + bcaDataList.size() + " BCA orders to send to AO");
            // iterate each order
            for (BcaData bcaData : bcaDataList) {
                orderBcaService.sendEmailOrder(bcaData);
            }
        }
        System.out.println("=================== END SEND BCA Data to AO ===================");
    }
}

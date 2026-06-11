package id.co.msiglife.pollux.schduler.job;

import id.co.msiglife.pollux.schduler.dto.PreProcessingKonfirmasiSuratDTO;
import id.co.msiglife.pollux.schduler.service.OrderBcaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderBcaScheduler {

    @Autowired
    private OrderBcaService orderBcaService;

    @Scheduled(cron = "0 * * * * *")
    public void sendOrderBcaToAo() {
        System.out.println("=================== START SEND BCA Data to AO ===================");
        orderBcaService.sendEmailOrder();
        System.out.println("=================== END SEND BCA Data to AO ===================");
    }
}

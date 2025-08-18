package id.co.msiglife.pollux.schduler.service;

import id.co.msiglife.pollux.schduler.entity.BcaData;
import id.co.msiglife.pollux.schduler.repository.MappingBcaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TakeoutPostponedDeclineService {

    @Autowired
    MappingBcaRepository mappingBcaRepository;

    @Scheduled(cron = "0 40 1 * * ?")
    public void takeoutPostponedDecline() {
        try {
            System.out.println("=================== START EXECUTING Takeout Postpone Decline Order BCA ===================");
            List<BcaData> dataTakeout = mappingBcaRepository.getDataTakeoutPosponedDecline();
            if (!dataTakeout.isEmpty()){
                for (int i = 0; i < dataTakeout.size(); i++) {
                    List<BcaData> dataForUpdate = mappingBcaRepository.getDataForUpdateOrderStatus(dataTakeout.get(i).getRegSpaj());
                    System.out.println("regSpaj : " + dataTakeout.get(i).getRegSpaj());
                    for (int j = 0; j < dataForUpdate.size(); j++) {
                        mappingBcaRepository.updateBcaDataOrderStatusAndUserByRegSpaj(dataForUpdate.get(j).getRegSpaj(), dataForUpdate.get(j).getOrderStatus(), dataForUpdate.get(j).getOrderStatus() + 1);
                    }
                }
            }
            System.out.println("=================== FINISHING EXECUTING Takeout Postpone Decline Order BCA ===================");
        } catch (RuntimeException e) {
            String message = !e.getMessage().isEmpty() ? e.getMessage() : "Error takeout Postponed / Decline in order bca";
            System.out.println("Error log: " + message);
            e.printStackTrace();
        }
    }
}
package id.co.msiglife.pollux.schduler.job;

import id.co.msiglife.pollux.schduler.dto.SuratAutoCancelDTO;
import id.co.msiglife.pollux.schduler.service.SuratTunggakanPremiService;
import id.co.msiglife.pollux.schduler.util.StaticValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SuratTunggakanPremiScheduler {

    @Autowired
    SuratTunggakanPremiService suratTunggakanPremiService;

    @Scheduled(cron = "0 20 1 * * ?")
    public void sendEmailTunggakanPremi(){
        List<SuratAutoCancelDTO> tunggakanPremiDTOList = suratTunggakanPremiService.getTunggakanPremi(StaticValue.TUNGGAKAN_PREMI_REMINDER_DAYS);
        System.out.println("=================== START GENERATE SURAT TUNGGAKAN PREMI PDF (" + tunggakanPremiDTOList.size() + ") Data ===================");
        suratTunggakanPremiService.generatePdfTunggakanPremi(tunggakanPremiDTOList);
        System.out.println("=================== END GENERATED SURAT TUNGGAKAN PREMI PDF ===================");
        System.out.println("=================== START SENDING EMAIL SURAT TUNGGAKAN PREMI PDF ===================");
        suratTunggakanPremiService.generateAutoMail(tunggakanPremiDTOList);
        System.out.println("=================== END SEND EMAIL SURAT TUNGGAKAN PREMI PDF ===================");
    }

    @Scheduled(cron = "0 20 5 * * ?")
    public void sendEmailTunggakanPremiRetry(){
        List<SuratAutoCancelDTO> tunggakanPremiDTOList = suratTunggakanPremiService.getTunggakanPremi(StaticValue.TUNGGAKAN_PREMI_REMINDER_DAYS);
        System.out.println("=================== RETRYING START GENERATE SURAT TUNGGAKAN PREMI PDF " + tunggakanPremiDTOList.size() + " Data ===================");
        suratTunggakanPremiService.generatePdfTunggakanPremi(tunggakanPremiDTOList);
        System.out.println("=================== RETRYING END GENERATED SURAT TUNGGAKAN PREMI PDF ===================");
        System.out.println("=================== RETRYING START SENDING EMAIL SURAT TUNGGAKAN PREMI PDF ===================");
        suratTunggakanPremiService.generateAutoMail(tunggakanPremiDTOList);
        System.out.println("=================== RETRYING END SEND EMAIL SURAT TUNGGAKAN PREMI PDF ===================");
    }
}

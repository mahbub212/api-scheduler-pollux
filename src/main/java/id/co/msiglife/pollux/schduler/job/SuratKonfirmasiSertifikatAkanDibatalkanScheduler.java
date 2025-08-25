package id.co.msiglife.pollux.schduler.job;

import id.co.msiglife.pollux.schduler.dto.SuratAutoCancelDTO;
import id.co.msiglife.pollux.schduler.service.SuratKonfirmasiSertifikatAkanDibatalkanService;
import id.co.msiglife.pollux.schduler.util.StaticValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Component
public class SuratKonfirmasiSertifikatAkanDibatalkanScheduler {

    @Autowired
    SuratKonfirmasiSertifikatAkanDibatalkanService suratKonfirmasiSertifikatAkanDibatalkanService;

    @Scheduled(cron = "0 0 1 * * ?")
    public void sendKonfirmasiSertifikatAkanDibatalkanEmail() throws Exception {
        System.out.println("Running Scheduler for Surat Konfirmasi Sertifikat akan Dibatalkan at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", new Locale("id", "ID"))));
        List<SuratAutoCancelDTO> sertifikatAkanDibatalkanDTOList = suratKonfirmasiSertifikatAkanDibatalkanService.getTunggakanPremi(StaticValue.SERTIFIKAT_AKAN_DIBATALKAN_REMINDER_DAYS);
        System.out.println("=================== START GENERATE SURAT KONFIRMASI SERTIFIKAT AKAN DIBATALKAN PDF (" + sertifikatAkanDibatalkanDTOList.size() + ") Data ===================");
        suratKonfirmasiSertifikatAkanDibatalkanService.generatePdfSuratKonfirmasiSertifikatAkanDibatalkan(sertifikatAkanDibatalkanDTOList);
        System.out.println("=================== END GENERATED SURAT KONFIRMASI SERTIFIKAT AKAN DIBATALKAN PDF ===================");
        System.out.println("=================== START SENDING EMAIL SURAT KONFIRMASI SERTIFIKAT AKAN DIBATALKAN PDF ===================");
        suratKonfirmasiSertifikatAkanDibatalkanService.generateAutoMail(sertifikatAkanDibatalkanDTOList);

        System.out.println("=================== END SEND EMAIL SURAT KONFIRMASI SERTIFIKAT AKAN DIBATALKAN PDF ===================");
    }

    @Scheduled(cron = "0 0 5 * * ?")
    public void retrySendingKonfirmasiSertifikatAkanDibatalkanEmail() throws Exception {
        System.out.println("Retrying Scheduler for Surat Konfirmasi Sertifikat akan Dibatalkan at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", new Locale("id", "ID"))));
        List<SuratAutoCancelDTO> sertifikatAkanDibatalkanDTOList = suratKonfirmasiSertifikatAkanDibatalkanService.getTunggakanPremi(StaticValue.SERTIFIKAT_AKAN_DIBATALKAN_REMINDER_DAYS);
        System.out.println("=================== RETRYING START GENERATE SURAT KONFIRMASI SERTIFIKAT AKAN DIBATALKAN PDF " + sertifikatAkanDibatalkanDTOList.size() + " Data ===================");
        suratKonfirmasiSertifikatAkanDibatalkanService.generatePdfSuratKonfirmasiSertifikatAkanDibatalkan(sertifikatAkanDibatalkanDTOList);
        System.out.println("=================== RETRYING END GENERATED SURAT KONFIRMASI SERTIFIKAT AKAN DIBATALKAN PDF ===================");
        System.out.println("=================== RETRYING START SENDING EMAIL SURAT KONFIRMASI SERTIFIKAT AKAN DIBATALKAN PDF ===================");
        suratKonfirmasiSertifikatAkanDibatalkanService.generateAutoMail(sertifikatAkanDibatalkanDTOList);
        System.out.println("=================== RETRYING END SEND EMAIL SURAT KONFIRMASI SERTIFIKAT AKAN DIBATALKAN PDF ===================");
    }

}

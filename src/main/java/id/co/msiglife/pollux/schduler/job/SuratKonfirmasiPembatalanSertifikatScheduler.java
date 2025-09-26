package id.co.msiglife.pollux.schduler.job;

import id.co.msiglife.pollux.schduler.dto.SuratAutoCancelDTO;
import id.co.msiglife.pollux.schduler.service.SuratKonfirmasiPembatalanSertifikatService;
import id.co.msiglife.pollux.schduler.util.StaticValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SuratKonfirmasiPembatalanSertifikatScheduler {

    @Autowired
    SuratKonfirmasiPembatalanSertifikatService suratKonfirmasiPembatalanSertifikatService;

    @Scheduled(cron = "0 40 0 * * ?")
    public void sendEmailSuratPembatalanSertifikat(){
        List<SuratAutoCancelDTO> pembatalanSertifikatDTOList = suratKonfirmasiPembatalanSertifikatService.getListSuratPembatalanSertifikat(StaticValue.SERTIFIKAT_DIBATALKAN_NOTIFICATION_DAYS);
        System.out.println("=================== START GENERATE SURAT KONFIRMASI PEMBATALAN SERTIFIKAT PDF (" + pembatalanSertifikatDTOList.size() + ") Data ===================");
        suratKonfirmasiPembatalanSertifikatService.generatePdfSuratPembatalanSertifikat(pembatalanSertifikatDTOList);
        System.out.println("=================== END GENERATED SURAT KONFIRMASI PEMBATALAN SERTIFIKAT PDF ===================");
        System.out.println("=================== START SENDING EMAIL SURAT KONFIRMASI PEMBATALAN SERTIFIKAT PDF ===================");
        suratKonfirmasiPembatalanSertifikatService.generateEmailSuratPembatalanSertifikat(pembatalanSertifikatDTOList);
        System.out.println("=================== END SEND EMAIL SURAT KONFIRMASI PEMBATALAN SERTIFIKAT PDF ===================");
    }

    @Scheduled(cron = "0 40 4 * * ?")
    public void sendEmailSuratPembatalanSertifikatRetry(){
        List<SuratAutoCancelDTO> pembatalanSertifikatDTOList = suratKonfirmasiPembatalanSertifikatService.getListSuratPembatalanSertifikat(StaticValue.SERTIFIKAT_DIBATALKAN_NOTIFICATION_DAYS);
        System.out.println("=================== RETRYING START GENERATE SURAT KONFIRMASI PEMBATALAN SERTIFIKAT PDF " + pembatalanSertifikatDTOList.size() + " Data ===================");
        suratKonfirmasiPembatalanSertifikatService.generatePdfSuratPembatalanSertifikat(pembatalanSertifikatDTOList);
        System.out.println("=================== RETRYING END GENERATED SURAT KONFIRMASI PEMBATALAN SERTIFIKAT PDF ===================");
        System.out.println("=================== RETRYING START SENDING EMAIL SURAT KONFIRMASI PEMBATALAN SERTIFIKAT PDF ===================");
        suratKonfirmasiPembatalanSertifikatService.generateEmailSuratPembatalanSertifikat(pembatalanSertifikatDTOList);
        System.out.println("=================== RETRYING END SEND EMAIL SURAT KONFIRMASI PEMBATALAN SERTIFIKAT PDF ===================");
    }

}

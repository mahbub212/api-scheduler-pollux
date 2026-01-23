package id.co.msiglife.pollux.schduler.job;

import id.co.msiglife.pollux.schduler.dto.SuratAutoCancelDTO;
import id.co.msiglife.pollux.schduler.service.CheckLineBusiness.CheckLineBusinessService;
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

    @Autowired
    private CheckLineBusinessService checkLineBusinessService;

    @Scheduled(cron = "0 40 0 * * ?")
    public void sendEmailSuratPembatalanSertifikat(){
        List<SuratAutoCancelDTO> pembatalanSertifikatDTOList = suratKonfirmasiPembatalanSertifikatService.getListSuratPembatalanSertifikat(StaticValue.SERTIFIKAT_DIBATALKAN_NOTIFICATION_DAYS);
        //Checking line business
        List<SuratAutoCancelDTO> konfirmasiAwalDTOListAfterChecking = checkLineBusinessService.checkLineBusiness(pembatalanSertifikatDTOList);

        System.out.println("=================== START GENERATE SURAT KONFIRMASI PEMBATALAN SERTIFIKAT PDF (" + konfirmasiAwalDTOListAfterChecking.size() + ") Data ===================");
        suratKonfirmasiPembatalanSertifikatService.generatePdfSuratPembatalanSertifikat(konfirmasiAwalDTOListAfterChecking);
        System.out.println("=================== END GENERATED SURAT KONFIRMASI PEMBATALAN SERTIFIKAT PDF ===================");
        System.out.println("=================== START SENDING EMAIL SURAT KONFIRMASI PEMBATALAN SERTIFIKAT PDF ===================");
        suratKonfirmasiPembatalanSertifikatService.generateEmailSuratPembatalanSertifikat(konfirmasiAwalDTOListAfterChecking);
        System.out.println("=================== END SEND EMAIL SURAT KONFIRMASI PEMBATALAN SERTIFIKAT PDF ===================");
    }

    @Scheduled(cron = "0 40 4 * * ?")
    public void sendEmailSuratPembatalanSertifikatRetry(){
        List<SuratAutoCancelDTO> pembatalanSertifikatDTOList = suratKonfirmasiPembatalanSertifikatService.getListSuratPembatalanSertifikat(StaticValue.SERTIFIKAT_DIBATALKAN_NOTIFICATION_DAYS);
        //Checking line business
        List<SuratAutoCancelDTO> konfirmasiAwalDTOListAfterChecking = checkLineBusinessService.checkLineBusiness(pembatalanSertifikatDTOList);

        System.out.println("=================== RETRYING START GENERATE SURAT KONFIRMASI PEMBATALAN SERTIFIKAT PDF " + konfirmasiAwalDTOListAfterChecking.size() + " Data ===================");
        suratKonfirmasiPembatalanSertifikatService.generatePdfSuratPembatalanSertifikat(konfirmasiAwalDTOListAfterChecking);
        System.out.println("=================== RETRYING END GENERATED SURAT KONFIRMASI PEMBATALAN SERTIFIKAT PDF ===================");
        System.out.println("=================== RETRYING START SENDING EMAIL SURAT KONFIRMASI PEMBATALAN SERTIFIKAT PDF ===================");
        suratKonfirmasiPembatalanSertifikatService.generateEmailSuratPembatalanSertifikat(konfirmasiAwalDTOListAfterChecking);
        System.out.println("=================== RETRYING END SEND EMAIL SURAT KONFIRMASI PEMBATALAN SERTIFIKAT PDF ===================");
    }

}

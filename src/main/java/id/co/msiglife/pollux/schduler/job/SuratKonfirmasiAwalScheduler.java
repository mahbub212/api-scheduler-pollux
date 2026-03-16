package id.co.msiglife.pollux.schduler.job;

import id.co.msiglife.pollux.schduler.dto.SuratAutoCancelDTO;
import id.co.msiglife.pollux.schduler.service.CheckLineBusiness.CheckLineBusinessService;
import id.co.msiglife.pollux.schduler.service.SuratKonfirmasiAwalService;
import id.co.msiglife.pollux.schduler.util.StaticValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SuratKonfirmasiAwalScheduler {

    @Autowired
    private SuratKonfirmasiAwalService suratKonfirmasiAwalService;

    @Autowired
    private CheckLineBusinessService checkLineBusinessService;

    @Scheduled(cron = "0 20 0 * * ?")
    public void sendEmailSuratKonfirmasiAwal(){
        List<SuratAutoCancelDTO> konfirmasiAwalDTOList = suratKonfirmasiAwalService.getListSuratKonfirmasiAwal(StaticValue.SURAT_KONFIRMASI_AWAL_REMINDER_TYPE);
        //Checking line business
        List<SuratAutoCancelDTO> konfirmasiAwalDTOListAfterChecking = checkLineBusinessService.checkLineBusiness(konfirmasiAwalDTOList);

        System.out.println("=================== START GENERATE SURAT KONFIRMASI AWAL PDF (" + konfirmasiAwalDTOListAfterChecking.size() + ") Data ===================");
        suratKonfirmasiAwalService.generatePdfSuratKonfirmasiAwal(konfirmasiAwalDTOListAfterChecking);
        System.out.println("=================== END GENERATED SURAT KONFIRMASI AWAL PDF ===================");
        System.out.println("=================== START SENDING EMAIL SURAT KONFIRMASI AWAL PDF ===================");
        suratKonfirmasiAwalService.generateEmailSuratKonfirmasiAwal(konfirmasiAwalDTOListAfterChecking);
        System.out.println("=================== END SEND EMAIL SURAT KONFIRMASI AWAL PDF ===================");
    }

    @Scheduled(cron = "0 20 4 * * ?")
    public void sendEmailSuratKonfirmasiAwalRetry(){
        List<SuratAutoCancelDTO> konfirmasiAwalDTOList = suratKonfirmasiAwalService.getListSuratKonfirmasiAwal(StaticValue.SURAT_KONFIRMASI_AWAL_REMINDER_TYPE);
        //Checking line business
        List<SuratAutoCancelDTO> konfirmasiAwalDTOListAfterChecking = checkLineBusinessService.checkLineBusiness(konfirmasiAwalDTOList);

        System.out.println("=================== RETRYING START GENERATE SURAT KONFIRMASI AWAL PDF " + konfirmasiAwalDTOListAfterChecking.size() + " Data ===================");
        suratKonfirmasiAwalService.generatePdfSuratKonfirmasiAwal(konfirmasiAwalDTOListAfterChecking);
        System.out.println("=================== RETRYING END GENERATED SURAT KONFIRMASI AWAL PDF ===================");
        System.out.println("=================== RETRYING START SENDING EMAIL SURAT KONFIRMASI AWAL PDF ===================");
        suratKonfirmasiAwalService.generateEmailSuratKonfirmasiAwal(konfirmasiAwalDTOListAfterChecking);
        System.out.println("=================== RETRYING END SEND EMAIL SURAT KONFIRMASI AWAL PDF ===================");
    }
}

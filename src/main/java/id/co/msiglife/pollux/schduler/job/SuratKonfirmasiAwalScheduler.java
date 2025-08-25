package id.co.msiglife.pollux.schduler.job;

import id.co.msiglife.pollux.schduler.dto.SuratAutoCancelDTO;
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

    @Scheduled(cron = "0 20 0 * * ?")
    public void sendEmailSuratKonfirmasiAwal(){
        List<SuratAutoCancelDTO> konfirmasiAwalDTOList = suratKonfirmasiAwalService.getListSuratKonfirmasiAwal(StaticValue.SURAT_KONFIRMASI_AWAL_REMINDER_DAYS);
        System.out.println("=================== START GENERATE SURAT KONFIRMASI AWAL PDF (" + konfirmasiAwalDTOList.size() + ") Data ===================");
        suratKonfirmasiAwalService.generatePdfSuratKonfirmasiAwal(konfirmasiAwalDTOList);
        System.out.println("=================== END GENERATED SURAT KONFIRMASI AWAL PDF ===================");
        System.out.println("=================== START SENDING EMAIL SURAT KONFIRMASI AWAL PDF ===================");
        suratKonfirmasiAwalService.generateEmailSuratKonfirmasiAwal(konfirmasiAwalDTOList);
        System.out.println("=================== END SEND EMAIL SURAT KONFIRMASI AWAL PDF ===================");
    }

    @Scheduled(cron = "0 20 4 * * ?")
    public void sendEmailSuratKonfirmasiAwalRetry(){
        List<SuratAutoCancelDTO> konfirmasiAwalDTOList = suratKonfirmasiAwalService.getListSuratKonfirmasiAwal(StaticValue.SURAT_KONFIRMASI_AWAL_REMINDER_DAYS);
        System.out.println("=================== RETRYING START GENERATE SURAT KONFIRMASI AWAL PDF " + konfirmasiAwalDTOList.size() + " Data ===================");
        suratKonfirmasiAwalService.generatePdfSuratKonfirmasiAwal(konfirmasiAwalDTOList);
        System.out.println("=================== RETRYING END GENERATED SURAT KONFIRMASI AWAL PDF ===================");
        System.out.println("=================== RETRYING START SENDING EMAIL SURAT KONFIRMASI AWAL PDF ===================");
        suratKonfirmasiAwalService.generateEmailSuratKonfirmasiAwal(konfirmasiAwalDTOList);
        System.out.println("=================== RETRYING END SEND EMAIL SURAT KONFIRMASI AWAL PDF ===================");
    }
}

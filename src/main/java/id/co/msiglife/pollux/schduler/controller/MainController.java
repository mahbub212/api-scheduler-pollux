package id.co.msiglife.pollux.schduler.controller;

import id.co.msiglife.pollux.schduler.dto.ApiResponseDTO;
import id.co.msiglife.pollux.schduler.job.*;
import id.co.msiglife.pollux.schduler.service.TakeoutPostponedDeclineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin
@RequiredArgsConstructor
public class MainController {

    private final TakeoutPostponedDeclineService takeoutPostponedDeclineService;

    private final CancelPolisScheduler cancelPolisScheduler;
    private final CancelSpajkScheduler cancelSpajkScheduler;
    private final SuratKonfirmasiPembatalanSertifikatScheduler suratKonfirmasiPembatalanSertifikatScheduler;
    private final PreProcessingAutoCancelScheduler preProcessingAutoCancelScheduler;
    private final SuratKonfirmasiAwalScheduler suratKonfirmasiAwalScheduler;
    private final SuratTunggakanPremiScheduler suratTunggakanPremiScheduler;
    private final SuratKonfirmasiSertifikatAkanDibatalkanScheduler suratKonfirmasiAkanDibatalkanScheduler;
    private final BcaRiplayScheduler bcaRiplayScheduler;

    @GetMapping("/api")
    public ResponseEntity<?> helloWorld() {
        try {
            return ResponseEntity.status(200).body(new ApiResponseDTO<String>(200, "Welcome to Pollux Scheduler Service API", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing response");
        }
    }

    @PostMapping("/api/test-takeout-and-history")
    public void takeoutPostponedDecline() {
            takeoutPostponedDeclineService.takeoutPostponedDecline();
    }

    @PostMapping("/api/test-cancel-polis")
    public void cancelPolist(){
        cancelPolisScheduler.cancelPolisScheduler();
    }

    @PostMapping("/api/test-cancel-spajk")
    public void cancelSpajk(){
        cancelSpajkScheduler.cancelSpajkScheduler();
    }

    @PostMapping("/api/test-pre-prosessing")
    public void preprosesingSuratKonfirmas(){
        preProcessingAutoCancelScheduler.preparationGenerateSuratKonfirmasi();
    }

    @GetMapping("/api/run-bca-riplay")
    public String run() throws Exception {
        bcaRiplayScheduler.bcaRiplayScheduler();
        return "OK";
    }

    @PostMapping("/api/test-pembatalan-sertifikat")
    public void suratPembatalanSertifikat(){
        suratKonfirmasiPembatalanSertifikatScheduler.sendEmailSuratPembatalanSertifikat();
    }

    @PostMapping("/api/test-surat-konfirmasi-sertifikat-akan-dibatalkan")
    public void suratKonfirmasiSertifikatAkanDibatalkan(){
        suratKonfirmasiAkanDibatalkanScheduler.sendKonfirmasiSertifikatAkanDibatalkanEmail();
    }

    @PostMapping("/api/test-surat-konfirmasi-awal")
    public void suratKonfirmasiAwal(){
        suratKonfirmasiAwalScheduler.sendEmailSuratKonfirmasiAwal();
    }

    @PostMapping("/api/test-surat-tunggakan-premi")
    public void suratTunggakanPremi(){
        suratTunggakanPremiScheduler.sendEmailTunggakanPremi();
    }

}

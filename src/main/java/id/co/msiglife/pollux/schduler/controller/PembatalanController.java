package id.co.msiglife.pollux.schduler.controller;

import id.co.msiglife.pollux.schduler.dto.ApiResponseDTO;
import id.co.msiglife.pollux.schduler.job.CancelPolisScheduler;
import id.co.msiglife.pollux.schduler.job.PreProcessingAutoCancelScheduler;
import id.co.msiglife.pollux.schduler.job.SuratKonfirmasiPembatalanSertifikatScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class PembatalanController {

    private final CancelPolisScheduler cancelPolisScheduler;
    private final SuratKonfirmasiPembatalanSertifikatScheduler suratKonfirmasiPembatalanSertifikatScheduler;
    private final PreProcessingAutoCancelScheduler preProcessingAutoCancelScheduler;

    @GetMapping("/api")
    public ResponseEntity<?> helloWorld() {
        try {
            return ResponseEntity.status(200).body(new ApiResponseDTO<String>(200, "Welcome to Pollux Scheduler Service API", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing response");
        }
    }


    @PostMapping("/pembatalan/test-cancel-polis")
    public void cancelPolist(){
        cancelPolisScheduler.cancelPolisScheduler();
    }

    @PostMapping("/pembatalan/test-pre-prosessing")
    public void preprosesingSuratKonfirmas(){
        preProcessingAutoCancelScheduler.preparationGenerateSuratKonfirmasi();
    }

    @PostMapping("/pembatalan/test-pembatalan-sertifikat")
    public void suratPembatalanSertifikat(){
        suratKonfirmasiPembatalanSertifikatScheduler.sendEmailSuratPembatalanSertifikat();
    }


}

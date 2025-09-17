package id.co.msiglife.pollux.schduler.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.msiglife.pollux.schduler.dto.ApiResponseDTO;
import id.co.msiglife.pollux.schduler.job.PreProcessingAutoCancelScheduler;
import id.co.msiglife.pollux.schduler.job.SuratKonfirmasiPembatalanSertifikatScheduler;
import id.co.msiglife.pollux.schduler.job.CancelPolisScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class MainController {

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

    @PostMapping("/api/test-cancel-polis")
    public void cancelPolist(){
        cancelPolisScheduler.cancelPolisScheduler();
    }

    @PostMapping("/api/test-pre-prosessing")
    public void preprosesingSuratKonfirmas(){
        preProcessingAutoCancelScheduler.preparationGenerateSuratKonfirmasi();
    }

    @PostMapping("/api/test-pembatalan-sertifikat")
    public void suratPembatalanSertifikat(){
        suratKonfirmasiPembatalanSertifikatScheduler.sendEmailSuratPembatalanSertifikat();
    }

}

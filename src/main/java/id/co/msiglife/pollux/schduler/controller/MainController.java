package id.co.msiglife.pollux.schduler.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.msiglife.pollux.schduler.dto.ApiResponseDTO;
import id.co.msiglife.pollux.schduler.job.*;
import id.co.msiglife.pollux.schduler.service.CancelSpajkService;
import id.co.msiglife.pollux.schduler.service.SuratKonfirmasiAwalService;
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
    private final CancelSpajkScheduler cancelSpajkScheduler;
    private final SuratKonfirmasiPembatalanSertifikatScheduler suratKonfirmasiPembatalanSertifikatScheduler;
    private final PreProcessingAutoCancelScheduler preProcessingAutoCancelScheduler;
    private final SuratKonfirmasiAwalScheduler suratKonfirmasiAwalScheduler;
    private final SuratTunggakanPremiScheduler suratTunggakanPremiScheduler;
    private final SuratKonfirmasiSertifikatAkanDibatalkanScheduler suratKonfirmasiAkanDibatalkanScheduler;

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

    @PostMapping("/api/test-cancel-spajk")
    public void cancelSpajk(){
        cancelSpajkScheduler.cancelSpajkScheduler();
    }

    @PostMapping("/api/test-pre-prosessing")
    public void preprosesingSuratKonfirmas(){
        preProcessingAutoCancelScheduler.preparationGenerateSuratKonfirmasi();
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

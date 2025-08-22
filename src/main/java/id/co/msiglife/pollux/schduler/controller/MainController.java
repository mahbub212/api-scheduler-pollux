package id.co.msiglife.pollux.schduler.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.msiglife.pollux.schduler.dto.ApiResponseDTO;
import id.co.msiglife.pollux.schduler.service.TakeoutPostponedDeclineService;
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

    private final TakeoutPostponedDeclineService takeoutPostponedDeclineService;

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

//    @PostMapping("/api/test-create-history")
//    public void createHistoryPostponeDeclineInHistory() {
//        takeoutPostponedDeclineService.createHistoryPostponeDeclineInHistory();
//    }
}

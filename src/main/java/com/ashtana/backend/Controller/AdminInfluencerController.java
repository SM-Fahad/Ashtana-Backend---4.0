package com.ashtana.backend.Controller;

import com.ashtana.backend.DTO.RequestDTO.InfluencerRequestDTO;
import com.ashtana.backend.DTO.ResponseDTO.InfluencerResponseDTO;
import com.ashtana.backend.Service.InfluencerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/influencers")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class AdminInfluencerController {

    private final InfluencerService influencerService;

    // Convert user to influencer
    @PostMapping("/convert/{userName}")
    public ResponseEntity<InfluencerResponseDTO> convertToInfluencer(
            @PathVariable String userName,
            @RequestParam(defaultValue = "10.0") Double commissionRate) {

        // Create request DTO
        InfluencerRequestDTO request = new InfluencerRequestDTO();
        request.setUserName(userName);
        request.setCommissionRate(commissionRate);
        request.setIsActive(true);

        return ResponseEntity.ok(influencerService.createInfluencer(request));
    }

    // Get all active influencers
    @GetMapping
    public ResponseEntity<List<InfluencerResponseDTO>> getAllActiveInfluencers() {
        return ResponseEntity.ok(influencerService.getAllActiveInfluencers());
    }

    // Get all influencers (active and inactive)
    @GetMapping("/all")
    public ResponseEntity<List<InfluencerResponseDTO>> getAllInfluencers() {
        return ResponseEntity.ok(influencerService.getAllInfluencers());
    }

    // Remove influencer
    @DeleteMapping("/{userName}")
    public ResponseEntity<String> removeInfluencer(@PathVariable String userName) {
        try {
            // Get influencer by username to find ID
            InfluencerResponseDTO influencer = influencerService.getInfluencerByUserName(userName);
            influencerService.deleteInfluencer(influencer.getId());
            return ResponseEntity.ok("User " + userName + " is no longer an influencer");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Check if user is influencer
    @GetMapping("/check/{userName}")
    public ResponseEntity<Boolean> isInfluencer(@PathVariable String userName) {
        try {
            influencerService.getInfluencerByUserName(userName);
            return ResponseEntity.ok(true);
        } catch (RuntimeException e) {
            return ResponseEntity.ok(false);
        }
    }
}
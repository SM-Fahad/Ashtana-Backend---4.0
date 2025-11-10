package com.ashtana.backend.Controller;

import com.ashtana.backend.DTO.ResponseDTO.CommissionResponseDTO;
import com.ashtana.backend.Service.CommissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commissions")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class CommissionController {

    private final CommissionService commissionService;

    @GetMapping("/influencer/{influencerId}")
    public ResponseEntity<List<CommissionResponseDTO>> getInfluencerCommissions(@PathVariable Long influencerId) {
        return ResponseEntity.ok(commissionService.getCommissionsByInfluencerId(influencerId));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<CommissionResponseDTO>> getPendingCommissions() {
        return ResponseEntity.ok(commissionService.getPendingCommissions());
    }

    @PutMapping("/{commissionId}/pay")
    public ResponseEntity<CommissionResponseDTO> markCommissionAsPaid(@PathVariable Long commissionId) {
        return ResponseEntity.ok(commissionService.markCommissionAsPaid(commissionId));
    }

    @PutMapping("/influencer/{influencerId}/pay-all")
    public ResponseEntity<Void> payAllPendingCommissions(@PathVariable Long influencerId) {
        commissionService.payAllPendingCommissions(influencerId);
        return ResponseEntity.ok().build();
    }
}
package com.ashtana.backend.Controller;

import com.ashtana.backend.DTO.RequestDTO.InfluencerRequestDTO;
import com.ashtana.backend.DTO.ResponseDTO.InfluencerResponseDTO;
import com.ashtana.backend.Service.InfluencerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/influencers")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class InfluencerController {

    private final InfluencerService influencerService;

    @PostMapping
    public ResponseEntity<InfluencerResponseDTO> createInfluencer(@RequestBody InfluencerRequestDTO dto) {
        return ResponseEntity.ok(influencerService.createInfluencer(dto));
    }

    @GetMapping
    public ResponseEntity<List<InfluencerResponseDTO>> getAllActiveInfluencers() {
        return ResponseEntity.ok(influencerService.getAllActiveInfluencers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InfluencerResponseDTO> getInfluencerById(@PathVariable Long id) {
        return ResponseEntity.ok(influencerService.getInfluencerById(id));
    }

    @GetMapping("/code/{influencerCode}")
    public ResponseEntity<InfluencerResponseDTO> getInfluencerByCode(@PathVariable String influencerCode) {
        return ResponseEntity.ok(influencerService.getInfluencerByCode(influencerCode));
    }

    @GetMapping("/user/{userName}")
    public ResponseEntity<InfluencerResponseDTO> getInfluencerByUserName(@PathVariable String userName) {
        return ResponseEntity.ok(influencerService.getInfluencerByUserName(userName));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InfluencerResponseDTO> updateInfluencer(
            @PathVariable Long id,
            @RequestBody InfluencerRequestDTO dto) {
        return ResponseEntity.ok(influencerService.updateInfluencer(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInfluencer(@PathVariable Long id) {
        influencerService.deleteInfluencer(id);
        return ResponseEntity.ok().build();
    }
}
package com.ashtana.backend.Service;

import com.ashtana.backend.DTO.RequestDTO.InfluencerRequestDTO;
import com.ashtana.backend.DTO.ResponseDTO.InfluencerResponseDTO;
import com.ashtana.backend.Entity.Influencer;
import com.ashtana.backend.Entity.User;
import com.ashtana.backend.Repository.InfluencerRepo;
import com.ashtana.backend.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class InfluencerService {

    private final InfluencerRepo influencerRepo;
    private final UserRepo userRepo;

    public InfluencerResponseDTO createInfluencer(InfluencerRequestDTO dto) {
        // Check if user exists
        User user = userRepo.findByUserName(dto.getUserName())
                .orElseThrow(() -> new RuntimeException("User not found: " + dto.getUserName()));

        // Check if user is already an influencer
        if (influencerRepo.existsByUserUserName(dto.getUserName())) {
            throw new RuntimeException("User is already an influencer");
        }

        // Create influencer
        Influencer influencer = new Influencer();
        influencer.setUser(user);
        influencer.setCommissionRate(dto.getCommissionRate());
        influencer.setIsActive(dto.getIsActive());

        Influencer savedInfluencer = influencerRepo.save(influencer);
        return toDto(savedInfluencer);
    }

    public InfluencerResponseDTO getInfluencerById(Long id) {
        Influencer influencer = influencerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Influencer not found"));
        return toDto(influencer);
    }

    public InfluencerResponseDTO getInfluencerByCode(String influencerCode) {
        Influencer influencer = influencerRepo.findByInfluencerCode(influencerCode)
                .orElseThrow(() -> new RuntimeException("Influencer not found with code: " + influencerCode));
        return toDto(influencer);
    }

    public InfluencerResponseDTO getInfluencerByUserName(String userName) {
        Influencer influencer = influencerRepo.findByUserUserName(userName)
                .orElseThrow(() -> new RuntimeException("Influencer not found for user: " + userName));
        return toDto(influencer);
    }

    public List<InfluencerResponseDTO> getAllActiveInfluencers() {
        return influencerRepo.findAll().stream()
                .filter(influencer -> Boolean.TRUE.equals(influencer.getIsActive()))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<InfluencerResponseDTO> getAllInfluencers() {
        return influencerRepo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public InfluencerResponseDTO updateInfluencer(Long id, InfluencerRequestDTO dto) {
        Influencer influencer = influencerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Influencer not found"));

        influencer.setCommissionRate(dto.getCommissionRate());
        influencer.setIsActive(dto.getIsActive());

        Influencer updatedInfluencer = influencerRepo.save(influencer);
        return toDto(updatedInfluencer);
    }

    public void deleteInfluencer(Long id) {
        Influencer influencer = influencerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Influencer not found"));
        influencerRepo.delete(influencer);
    }

    public InfluencerResponseDTO toDto(Influencer influencer) {
        InfluencerResponseDTO dto = new InfluencerResponseDTO();
        dto.setId(influencer.getId());
        dto.setInfluencerCode(influencer.getInfluencerCode());
        dto.setCommissionRate(influencer.getCommissionRate());
        dto.setTotalEarnings(influencer.getTotalEarnings());
        dto.setPendingEarnings(influencer.getPendingEarnings());
        dto.setIsActive(influencer.getIsActive());
        dto.setCreatedAt(influencer.getCreatedAt());

        // User details
        if (influencer.getUser() != null) {
            dto.setUserName(influencer.getUser().getUserName());
            dto.setUserFirstName(influencer.getUser().getUserFirstName());
            dto.setUserLastName(influencer.getUser().getUserLastName());
            dto.setUserEmail(influencer.getUser().getEmail());
        }

        // Initialize collections to avoid null pointers
        initializeCollections(influencer);

        // Set stats - now safe because collections are initialized
        dto.setTotalCoupons((long) influencer.getCoupons().size());
        dto.setTotalCommissions((long) influencer.getCommissions().size());
        dto.setPaidCommissions(influencer.getCommissions().stream()
                .filter(c -> "PAID".equals(c.getStatus()))
                .count());

        return dto;
    }

    // Helper method to initialize collections if null
    private void initializeCollections(Influencer influencer) {
        if (influencer.getCoupons() == null) {
            influencer.setCoupons(new java.util.ArrayList<>());
        }
        if (influencer.getCommissions() == null) {
            influencer.setCommissions(new java.util.ArrayList<>());
        }
    }
}
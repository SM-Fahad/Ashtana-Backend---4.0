package com.ashtana.backend.Service;

import com.ashtana.backend.DTO.ResponseDTO.InfluencerResponseDTO;
import com.ashtana.backend.Entity.Influencer;
import com.ashtana.backend.Entity.User;
import com.ashtana.backend.Repository.InfluencerRepo;
import com.ashtana.backend.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminInfluencerService {

    private final UserRepo userRepo;
    private final InfluencerRepo influencerRepo;

    public InfluencerResponseDTO convertUserToInfluencer(String userName, Double commissionRate) {
        // 1. Find user
        User user = userRepo.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("User not found: " + userName));

        // 2. Check if already an influencer
        if (influencerRepo.existsByUserUserName(userName)) {
            throw new RuntimeException("User is already an influencer");
        }

        // 3. Create influencer
        Influencer influencer = new Influencer();
        influencer.setUser(user);
        influencer.setCommissionRate(commissionRate);
        influencer.setIsActive(true);

        Influencer savedInfluencer = influencerRepo.save(influencer);

        // 4. Convert to DTO and return
        return toDto(savedInfluencer);
    }

    public String removeInfluencer(String userName) {
        // 1. Find influencer
        Influencer influencer = influencerRepo.findByUserUserName(userName)
                .orElseThrow(() -> new RuntimeException("Influencer not found for user: " + userName));

        // 2. Delete influencer
        influencerRepo.delete(influencer);

        return "User " + userName + " is no longer an influencer";
    }

    public boolean isUserInfluencer(String userName) {
        return influencerRepo.existsByUserUserName(userName);
    }

    private InfluencerResponseDTO toDto(Influencer influencer) {
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

        return dto;
    }
}
package com.ashtana.backend.DTO.ResponseDTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InfluencerResponseDTO {
    private Long id;
    private String influencerCode;
    private Double commissionRate;
    private Double totalEarnings;
    private Double pendingEarnings;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private String userName;
    private String userFirstName;
    private String userLastName;
    private String userEmail;

    // Make sure these fields exist and have proper getters/setters
    private Long totalCoupons = 0L;
    private Long totalCommissions = 0L;
    private Long paidCommissions = 0L;
}
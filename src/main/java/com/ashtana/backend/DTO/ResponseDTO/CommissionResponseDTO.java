package com.ashtana.backend.DTO.ResponseDTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommissionResponseDTO {
    private Long id;
    private Double amount;
    private String status;
    private Double commissionRate;
    private Double orderAmount;
    private String orderNumber;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private Long influencerId;
    private String influencerCode;
    private String couponCode;
    private Long orderId; // Added for completeness
}
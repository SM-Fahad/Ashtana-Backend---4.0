package com.ashtana.backend.DTO.RequestDTO;

import lombok.Data;

@Data
public class InfluencerRequestDTO {
    private String userName;
    private Double commissionRate = 10.0;
    private Boolean isActive = true;
}
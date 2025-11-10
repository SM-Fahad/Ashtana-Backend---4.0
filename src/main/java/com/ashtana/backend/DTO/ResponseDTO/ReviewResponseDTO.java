package com.ashtana.backend.DTO.ResponseDTO;

import lombok.Data;

@Data
public class ReviewResponseDTO {
    private Long id;
    private String userName;
    private String productName;
    private String comment;
    private Integer rating;
}

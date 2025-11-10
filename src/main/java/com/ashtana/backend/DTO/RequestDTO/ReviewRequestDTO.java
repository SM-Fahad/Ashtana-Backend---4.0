package com.ashtana.backend.DTO.RequestDTO;

import lombok.Data;

@Data
public class ReviewRequestDTO {
    private String userName;
    private Long productId;
    private String comment;
    private Integer rating;
}

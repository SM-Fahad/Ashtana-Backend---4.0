package com.ashtana.backend.DTO.RequestDTO;

import lombok.Data;

@Data
public class MyBagItemRequestDTO {
    private String userName; // ✅ Change from MyBagId to userName
    private Long productId;
    private Integer quantity;
    // ✅ Remove UserId - redundant
}
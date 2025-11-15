package com.ashtana.backend.DTO.ResponseDTO;

import lombok.Data;

import java.util.List;

@Data
public class MyBagItemResponseDTO {
    private Long id;
    private String productName;
    private Double pricePerItem; // ✅ Add this field
    private Integer quantity;
    private Double totalPrice;
    private Long productId; // ✅ Add product ID for frontend

    // ADD THESE FIELDS for product images
    private List<String> productImageUrls;
    private String primaryImageUrl; // Convenience field
}
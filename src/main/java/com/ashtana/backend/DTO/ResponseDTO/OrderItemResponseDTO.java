package com.ashtana.backend.DTO.ResponseDTO;

import lombok.Data;

@Data
public class OrderItemResponseDTO {
    private Long id;
    private Long productId;
    private String productName;
    private String productImage; // Optional
    private Integer quantity;
    private Double pricePerItem;
    private Double totalPrice;

    // ✅ Helper method for display
    public String getDisplayText() {
        return String.format("%s (Qty: %d) - $%.2f", productName, quantity, totalPrice);
    }
}
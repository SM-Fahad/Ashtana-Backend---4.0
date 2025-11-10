package com.ashtana.backend.DTO.RequestDTO;

import lombok.Data;

@Data
public class PaymentRequestDTO {
    private Long orderId;
    private String paymentMethod; // "COD", "CREDIT_CARD", "PAYPAL", etc.
    private Double amount;
    private String paymentToken; // For payment gateways
}
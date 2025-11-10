package com.ashtana.backend.DTO.ResponseDTO;

import com.ashtana.backend.Enums.PaymentStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PaymentResponseDTO {
    private Long id;
    private Long orderId;
    private Double amount;
    private String paymentMethod;
    private PaymentStatus paymentStatus;
    private LocalDateTime paymentDate;
}
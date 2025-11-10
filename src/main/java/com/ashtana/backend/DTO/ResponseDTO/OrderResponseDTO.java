package com.ashtana.backend.DTO.ResponseDTO;

import com.ashtana.backend.Entity.Address;
import com.ashtana.backend.Enums.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDTO {
    private Long id;
    private String orderNumber;
    private String userName;
    private Double subtotalAmount;
    private Double shippingCost;
    private Double taxAmount;
    private Double discountAmount;
    private Double totalAmount;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private Address shippingAddress;
    private Address billingAddress;
    private String couponCode;
    private String paymentMethod;
    private String paymentStatus;
    private List<OrderItemResponseDTO> items;
}
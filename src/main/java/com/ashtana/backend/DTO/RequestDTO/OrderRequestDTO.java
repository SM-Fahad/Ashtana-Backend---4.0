package com.ashtana.backend.DTO.RequestDTO;

import com.ashtana.backend.Entity.Address;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {
    private String userName;
    private List<OrderItemRequestDTO> items; // productId + quantity
    private Address shippingAddress;
}

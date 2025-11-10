package com.ashtana.backend.DTO.ResponseDTO;

import lombok.Data;

import java.util.List;

@Data
public class MyBagResponseDTO {
    private Long id;
    private String userName;
    private Integer totalItems;
    private Double totalPrice;
    private List<MyBagItemResponseDTO> items;
}
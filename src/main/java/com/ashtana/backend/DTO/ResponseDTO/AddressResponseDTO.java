package com.ashtana.backend.DTO.ResponseDTO;


import com.ashtana.backend.Enums.AddressType;
import lombok.Data;

@Data
public class AddressResponseDTO {
    private Long id;
    private String userName;
    private String recipientName;
    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private AddressType type;
    private String phone;
}
package com.ashtana.backend.DTO.RequestDTO;


import com.ashtana.backend.Enums.AddressType;
import lombok.Data;

@Data
public class AddressRequestDTO {
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
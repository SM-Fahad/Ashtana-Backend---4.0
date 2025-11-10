package com.ashtana.backend.DTO.RequestDTO;

import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.List;

@Data
public class UserUpdateRequestDTO {
    private String fullName;

    @Email(message = "Invalid email format")
    private String email;

    private String phone;
    private String profileImage;


    private List<AddressRequestDTO> addresses;
}

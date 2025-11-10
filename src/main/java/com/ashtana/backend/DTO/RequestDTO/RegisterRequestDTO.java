package com.ashtana.backend.DTO.RequestDTO;

import lombok.Data;

@Data
public class RegisterRequestDTO {
    private String fullName;
    private String email;
    private String phone;
    private String password;
}

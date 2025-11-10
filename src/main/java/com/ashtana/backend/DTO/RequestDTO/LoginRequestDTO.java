package com.ashtana.backend.DTO.RequestDTO;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String email;
    private String password;
}

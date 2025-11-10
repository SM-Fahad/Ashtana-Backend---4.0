package com.ashtana.backend.DTO.RequestDTO;


import com.ashtana.backend.Enums.AccountStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserRequestDTO {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 10, max = 15, message = "Phone number must be between 10–15 characters")
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private String profileImage;

    private AccountStatus accountStatus;

    // Optional during registration (user may leave blank)
//    private List<AddressRequestDTO> addresses;

//    private Boolean isActive;
//
//    private LocalDateTime createdAt;
//
//    private LocalDateTime updatedAt;
//
//    private String createdBy;


}

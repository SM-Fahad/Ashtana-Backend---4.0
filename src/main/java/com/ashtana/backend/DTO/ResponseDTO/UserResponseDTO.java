package com.ashtana.backend.DTO.ResponseDTO;


import com.ashtana.backend.Enums.AccountStatus;
import com.ashtana.backend.Enums.Role;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserResponseDTO {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private Role role;
//    private Boolean isActive;
    private AccountStatus accountStatus;

    private String profileImage;

    private List<AddressResponseDTO> addresses;
}

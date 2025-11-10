package com.ashtana.backend.DTO.secqurityDTO;


import com.ashtana.backend.Entity.User;

public record JwtResponse(
        String jwtToken,
        User user
//        String username,
//        String email,
//        Collection<String> roles
) {}
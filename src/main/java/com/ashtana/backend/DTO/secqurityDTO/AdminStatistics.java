package com.ashtana.backend.DTO.secqurityDTO;


import com.ashtana.backend.Entity.Role;

import java.util.List;

public record AdminStatistics(long totalUsers, long enabledUsers, List<Role> roles) {
}

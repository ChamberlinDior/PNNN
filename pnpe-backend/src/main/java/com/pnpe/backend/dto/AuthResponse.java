package com.pnpe.backend.dto;

public record AuthResponse(
        String token,
        String tokenType,
        Long userId,
        Long agentProfileId,
        String fullName,
        String email,
        String role,
        Long agencyId,
        String agencyName,
        Long departmentId,
        String departmentName
) {}
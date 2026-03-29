package com.pnpe.backend.dto;

public record UserSummaryDto(
        Long id,
        String fullName,
        String email,
        String role,
        String agency,
        String department,
        String jobTitle
) {}

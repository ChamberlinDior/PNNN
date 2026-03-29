package com.pnpe.backend.dto;

public record CounselorReferenceResponse(
        Long id,
        String agentCode,
        String specialty,
        Long userId,
        String fullName,
        String email
) {}
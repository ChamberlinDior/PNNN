package com.pnpe.backend.dto;

public record CounselorPerformanceResponse(
        Long counselorId,
        String counselorName,
        String agencyName,
        long portfolioSize,
        long totalInterviews,
        long totalPlacements,
        long totalActions
) {}
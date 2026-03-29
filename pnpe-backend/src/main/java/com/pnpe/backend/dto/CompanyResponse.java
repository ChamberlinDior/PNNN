package com.pnpe.backend.dto;

import com.pnpe.backend.model.enums.CompanyStatus;

public record CompanyResponse(
        Long id,
        String name,
        String sector,
        String city,
        String address,
        String contactName,
        String contactEmail,
        String contactPhone,
        String partnershipNotes,
        CompanyStatus status,
        long totalInterviews,
        long totalHires
) {}

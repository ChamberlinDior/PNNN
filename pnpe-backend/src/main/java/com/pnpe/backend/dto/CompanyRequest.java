package com.pnpe.backend.dto;

import com.pnpe.backend.model.enums.CompanyStatus;
import jakarta.validation.constraints.NotBlank;

public record CompanyRequest(
        @NotBlank String name,
        String sector,
        String city,
        String address,
        String contactName,
        String contactEmail,
        String contactPhone,
        String partnershipNotes,
        CompanyStatus status
) {}

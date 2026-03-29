package com.pnpe.backend.dto;

import com.pnpe.backend.model.enums.ContractType;
import com.pnpe.backend.model.enums.EmploymentStatus;

import java.time.LocalDate;

public record PlacementResponse(
        Long id,
        String jobSeekerName,
        String companyName,
        String counselorName,
        String positionTitle,
        LocalDate startDate,
        ContractType contractType,
        EmploymentStatus employmentStatus,
        String notes
) {}

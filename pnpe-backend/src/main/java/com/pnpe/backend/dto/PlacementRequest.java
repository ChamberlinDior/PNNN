package com.pnpe.backend.dto;

import com.pnpe.backend.model.enums.ContractType;
import com.pnpe.backend.model.enums.EmploymentStatus;

import java.time.LocalDate;

public record PlacementRequest(
        Long jobSeekerId,
        Long companyId,
        Long counselorId,
        Long interviewId,
        String positionTitle,
        LocalDate startDate,
        ContractType contractType,
        EmploymentStatus employmentStatus,
        String notes
) {}

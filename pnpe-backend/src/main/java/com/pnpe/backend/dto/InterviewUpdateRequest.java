package com.pnpe.backend.dto;

import com.pnpe.backend.model.enums.ContractType;
import com.pnpe.backend.model.enums.InterviewStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record InterviewUpdateRequest(
        @NotNull InterviewStatus status,
        String feedback,
        String applicantFeedback,
        String companyFeedback,
        String noShowReason,
        String followUpDecision,
        LocalDateTime followUpAt,
        ContractType proposedContractType,
        LocalDateTime hiredAt
) {}

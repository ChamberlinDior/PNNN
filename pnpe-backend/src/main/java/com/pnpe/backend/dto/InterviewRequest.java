package com.pnpe.backend.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record InterviewRequest(
        @NotNull Long jobSeekerId,
        @NotNull Long companyId,
        @NotNull Long agentProfileId,
        @NotBlank String jobTitle,
        @NotNull @Future LocalDateTime interviewDate,
        String location,
        String mode
) {}

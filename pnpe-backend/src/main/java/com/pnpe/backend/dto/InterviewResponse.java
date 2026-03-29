package com.pnpe.backend.dto;

import com.pnpe.backend.model.enums.InterviewStatus;

import java.time.LocalDateTime;

public record InterviewResponse(
        Long id,
        String jobSeekerName,
        String companyName,
        String agentName,
        String jobTitle,
        LocalDateTime interviewDate,
        String location,
        String mode,
        InterviewStatus status,
        String followUpDecision
) {}

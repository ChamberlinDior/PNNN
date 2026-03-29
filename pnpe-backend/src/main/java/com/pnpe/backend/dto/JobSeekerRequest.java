package com.pnpe.backend.dto;

import com.pnpe.backend.model.enums.Gender;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record JobSeekerRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        Gender gender,
        LocalDate dateOfBirth,
        String phone,
        String email,
        String city,
        String address,
        String educationLevel,
        String primarySkill,
        String projectSummary,
        String actionPlanSummary,
        Boolean selfRegistered,
        Long agencyId,
        Long assignedAgentId,
        Long preRegistrationId
) {}

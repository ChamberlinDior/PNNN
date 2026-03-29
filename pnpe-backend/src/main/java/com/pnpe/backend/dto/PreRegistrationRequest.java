package com.pnpe.backend.dto;

import com.pnpe.backend.model.enums.RegistrationChannel;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record PreRegistrationRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        String phone,
        String email,
        String city,
        LocalDate dateOfBirth,
        String educationLevel,
        String primarySkill,
        Boolean autonomousOnPortal,
        Boolean hasRequiredDocuments,
        String projectSummary,
        String welcomeNotes,
        Long agencyId,
        Long referredCounselorId,
        RegistrationChannel registrationChannel
) {}

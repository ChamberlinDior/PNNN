package com.pnpe.backend.dto;

import com.pnpe.backend.model.enums.RegistrationChannel;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
        RegistrationChannel registrationChannel,

        /**
         * Rendez-vous demandé par le demandeur.
         * Exemple : 2026-04-08T08:30
         */
        LocalDateTime appointmentAt
) {}
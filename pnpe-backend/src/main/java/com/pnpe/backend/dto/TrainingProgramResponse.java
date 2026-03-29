package com.pnpe.backend.dto;

import com.pnpe.backend.model.enums.TrainingStatus;

import java.time.LocalDate;

public record TrainingProgramResponse(
        Long id,
        String title,
        String description,
        String trainerName,
        String location,
        LocalDate startDate,
        LocalDate endDate,
        Integer capacity,
        TrainingStatus status,
        String agencyName,
        long enrollmentCount
) {}

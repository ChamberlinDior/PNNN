package com.pnpe.backend.dto;

import com.pnpe.backend.model.enums.TrainingStatus;
import java.time.LocalDate;

public record TrainingProgramRequest(
        String title,
        String description,
        String trainerName,
        String location,
        LocalDate startDate,
        LocalDate endDate,
        Integer capacity,
        TrainingStatus status,
        Long agencyId
) {}

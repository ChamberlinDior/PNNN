package com.pnpe.backend.dto;

public record TrainingEnrollmentRequest(
        Long trainingProgramId,
        Long jobSeekerId
) {}

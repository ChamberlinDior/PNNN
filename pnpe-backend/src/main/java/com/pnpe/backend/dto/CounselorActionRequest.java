package com.pnpe.backend.dto;

import com.pnpe.backend.model.enums.CounselorActionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CounselorActionRequest(
        @NotNull Long jobSeekerId,
        @NotNull Long counselorId,
        @NotNull CounselorActionType actionType,
        LocalDateTime actionDate,
        @NotBlank String summary,
        String details,
        LocalDateTime nextActionDate
) {}

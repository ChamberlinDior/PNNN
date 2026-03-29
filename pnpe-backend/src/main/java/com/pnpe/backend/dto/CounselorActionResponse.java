package com.pnpe.backend.dto;

import com.pnpe.backend.model.enums.CounselorActionType;

import java.time.LocalDateTime;

public record CounselorActionResponse(
        Long id,
        Long jobSeekerId,
        String jobSeekerName,
        Long counselorId,
        String counselorName,
        CounselorActionType actionType,
        LocalDateTime actionDate,
        String summary,
        String details,
        LocalDateTime nextActionDate
) {}

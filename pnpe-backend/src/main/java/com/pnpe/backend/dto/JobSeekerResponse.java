package com.pnpe.backend.dto;

import com.pnpe.backend.model.enums.JobSeekerStatus;

public record JobSeekerResponse(
        Long id,
        String dossierNumber,
        String openNumber,
        String firstName,
        String lastName,
        String phone,
        String email,
        String city,
        String primarySkill,
        JobSeekerStatus status,
        String agencyName,
        String assignedAgentName,
        Integer noShowCount,
        Boolean selfRegistered
) {}

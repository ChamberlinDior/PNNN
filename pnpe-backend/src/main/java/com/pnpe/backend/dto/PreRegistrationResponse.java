package com.pnpe.backend.dto;

import com.pnpe.backend.model.enums.PreRegistrationStatus;
import com.pnpe.backend.model.enums.RegistrationChannel;

import java.time.LocalDateTime;

public record PreRegistrationResponse(
        Long id,
        String requestNumber,
        String firstName,
        String lastName,
        String phone,
        String email,
        String city,
        RegistrationChannel registrationChannel,
        PreRegistrationStatus status,
        String agencyName,
        String counselorName,
        LocalDateTime submittedAt,
        Boolean documentsVerifiedByScan,
        LocalDateTime documentsVerifiedAt,
        String documentsVerifiedByName,
        PnpeCardResponse pnpeCard
) {}
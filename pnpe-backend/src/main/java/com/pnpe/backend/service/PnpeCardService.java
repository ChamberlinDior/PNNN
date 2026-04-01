package com.pnpe.backend.service;

import com.pnpe.backend.dto.PnpeCardResponse;

public interface PnpeCardService {
    PnpeCardResponse createForPreRegistration(Long preRegistrationId, Long scannerUserId);
    PnpeCardResponse findByPreRegistrationId(Long preRegistrationId);
    PnpeCardResponse findByPreRegistrationIdOrNull(Long preRegistrationId);
}
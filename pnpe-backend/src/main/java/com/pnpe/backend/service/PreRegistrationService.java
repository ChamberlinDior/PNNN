package com.pnpe.backend.service;

import com.pnpe.backend.dto.PreRegistrationRequest;
import com.pnpe.backend.dto.PreRegistrationResponse;

import java.util.List;

public interface PreRegistrationService {

    PreRegistrationResponse create(PreRegistrationRequest request);

    List<PreRegistrationResponse> findAll();

    // étape accueil / scan
    PreRegistrationResponse markReadyForCounselor(Long preRegistrationId, Long counselorId);

    // étape conseiller : prise en charge / mise en relation
    PreRegistrationResponse confirmCounselorConnection(Long preRegistrationId, Long counselorId);

    // étape finale conseiller : conversion
    PreRegistrationResponse validateAndConvert(Long preRegistrationId, Long counselorId);
}
package com.pnpe.backend.service;

import com.pnpe.backend.dto.PreRegistrationRequest;
import com.pnpe.backend.dto.PreRegistrationResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PreRegistrationService {

    PreRegistrationResponse create(PreRegistrationRequest request);

    List<PreRegistrationResponse> findAll();

    List<LocalDateTime> getAvailableAppointmentSlots(LocalDate date);

    // étape scan finale : attribution + transmission + génération carte PNPE
    PreRegistrationResponse markReadyForCounselor(Long preRegistrationId, Long counselorId, Long scannerUserId);

    // étape conseiller : prise en charge réelle
    PreRegistrationResponse confirmCounselorConnection(Long preRegistrationId, Long counselorId);

    // étape finale conseiller : conversion
    PreRegistrationResponse validateAndConvert(Long preRegistrationId, Long counselorId);

    // option conservée : validation documentaire simple
    PreRegistrationResponse validateDocumentsByScanner(Long preRegistrationId, Long scannerUserId);
}
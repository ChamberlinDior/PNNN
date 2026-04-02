package com.pnpe.backend.controller;

import com.pnpe.backend.dto.PreRegistrationRequest;
import com.pnpe.backend.dto.PreRegistrationResponse;
import com.pnpe.backend.service.PreRegistrationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/pre-registrations")
@CrossOrigin(origins = "*")
public class PreRegistrationController {

    private final PreRegistrationService preRegistrationService;

    public PreRegistrationController(PreRegistrationService preRegistrationService) {
        this.preRegistrationService = preRegistrationService;
    }

    @GetMapping
    public List<PreRegistrationResponse> findAll() {
        return preRegistrationService.findAll();
    }

    @GetMapping("/available-slots")
    public List<LocalDateTime> getAvailableSlots(@RequestParam LocalDate date) {
        return preRegistrationService.getAvailableAppointmentSlots(date);
    }

    @PostMapping
    public PreRegistrationResponse create(@Valid @RequestBody PreRegistrationRequest request) {
        return preRegistrationService.create(request);
    }

    @PostMapping("/{id}/ready-for-counselor")
    public PreRegistrationResponse markReadyForCounselor(@PathVariable Long id,
                                                         @RequestParam Long counselorId,
                                                         @RequestParam Long scannerUserId) {
        return preRegistrationService.markReadyForCounselor(id, counselorId, scannerUserId);
    }

    @PostMapping("/{id}/confirm-counselor-connection")
    public PreRegistrationResponse confirmCounselorConnection(@PathVariable Long id,
                                                              @RequestParam Long counselorId) {
        return preRegistrationService.confirmCounselorConnection(id, counselorId);
    }

    @PostMapping("/{id}/validate")
    public PreRegistrationResponse validate(@PathVariable Long id,
                                            @RequestParam Long counselorId) {
        return preRegistrationService.validateAndConvert(id, counselorId);
    }

    @PostMapping("/{id}/validate-documents")
    public PreRegistrationResponse validateDocumentsByScanner(@PathVariable Long id,
                                                              @RequestParam Long scannerUserId) {
        return preRegistrationService.validateDocumentsByScanner(id, scannerUserId);
    }
}
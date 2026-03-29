package com.pnpe.backend.controller;

import com.pnpe.backend.dto.PreRegistrationRequest;
import com.pnpe.backend.dto.PreRegistrationResponse;
import com.pnpe.backend.service.PreRegistrationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/pre-registrations")
@CrossOrigin(origins = "*")
public class PublicPreRegistrationController {

    private final PreRegistrationService preRegistrationService;

    public PublicPreRegistrationController(PreRegistrationService preRegistrationService) {
        this.preRegistrationService = preRegistrationService;
    }

    @PostMapping
    public PreRegistrationResponse create(@Valid @RequestBody PreRegistrationRequest request) {
        return preRegistrationService.create(request);
    }
}

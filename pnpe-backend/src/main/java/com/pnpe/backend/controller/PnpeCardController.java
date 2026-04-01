package com.pnpe.backend.controller;

import com.pnpe.backend.dto.PnpeCardResponse;
import com.pnpe.backend.service.PnpeCardService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pnpe-cards")
@CrossOrigin(origins = "*")
public class PnpeCardController {

    private final PnpeCardService pnpeCardService;

    public PnpeCardController(PnpeCardService pnpeCardService) {
        this.pnpeCardService = pnpeCardService;
    }

    @GetMapping("/pre-registration/{preRegistrationId}")
    public PnpeCardResponse findByPreRegistration(@PathVariable Long preRegistrationId) {
        return pnpeCardService.findByPreRegistrationId(preRegistrationId);
    }
}
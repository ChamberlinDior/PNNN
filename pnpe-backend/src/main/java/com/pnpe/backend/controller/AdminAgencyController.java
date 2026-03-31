package com.pnpe.backend.controller;

import com.pnpe.backend.dto.admin.*;
import com.pnpe.backend.service.AgencyAdminService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/agencies")
@CrossOrigin(origins = "*")
public class AdminAgencyController {

    private final AgencyAdminService agencyAdminService;

    public AdminAgencyController(AgencyAdminService agencyAdminService) {
        this.agencyAdminService = agencyAdminService;
    }

    @PostMapping
    public AgencyResponse createAgency(@Valid @RequestBody CreateAgencyRequest request) {
        return agencyAdminService.createAgency(request);
    }

    @PutMapping("/{agencyId}")
    public AgencyResponse updateAgency(@PathVariable Long agencyId,
                                       @Valid @RequestBody UpdateAgencyRequest request) {
        return agencyAdminService.updateAgency(agencyId, request);
    }

    @PatchMapping("/{agencyId}/status")
    public AgencyResponse updateAgencyStatus(@PathVariable Long agencyId,
                                             @Valid @RequestBody AgencyStatusUpdateRequest request) {
        return agencyAdminService.updateAgencyStatus(agencyId, request);
    }

    @GetMapping
    public List<AgencyResponse> getAllAgencies() {
        return agencyAdminService.getAllAgencies();
    }

    @GetMapping("/{agencyId}")
    public AgencyResponse getAgencyById(@PathVariable Long agencyId) {
        return agencyAdminService.getAgencyById(agencyId);
    }

    @GetMapping("/{agencyId}/departments")
    public List<DepartmentOptionResponse> getAgencyDepartments(@PathVariable Long agencyId) {
        return agencyAdminService.getAgencyDepartments(agencyId);
    }
}
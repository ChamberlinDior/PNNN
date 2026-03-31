package com.pnpe.backend.controller;

import com.pnpe.backend.dto.admin.*;
import com.pnpe.backend.service.AgencyUserAdminService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminAgencyUserController {

    private final AgencyUserAdminService agencyUserAdminService;

    public AdminAgencyUserController(AgencyUserAdminService agencyUserAdminService) {
        this.agencyUserAdminService = agencyUserAdminService;
    }

    @PostMapping("/agencies/{agencyId}/users")
    public AgencyUserResponse createUserInAgency(@PathVariable Long agencyId,
                                                 @Valid @RequestBody CreateAgencyUserRequest request) {
        return agencyUserAdminService.createUserInAgency(agencyId, request);
    }

    @PutMapping("/users/{userId}")
    public AgencyUserResponse updateUser(@PathVariable Long userId,
                                         @Valid @RequestBody UpdateAgencyUserRequest request) {
        return agencyUserAdminService.updateUser(userId, request);
    }

    @PatchMapping("/users/{userId}/status")
    public AgencyUserResponse updateUserStatus(@PathVariable Long userId,
                                               @Valid @RequestBody UserStatusUpdateRequest request) {
        return agencyUserAdminService.updateUserStatus(userId, request);
    }

    @GetMapping("/users/{userId}")
    public AgencyUserResponse getUserById(@PathVariable Long userId) {
        return agencyUserAdminService.getUserById(userId);
    }

    @GetMapping("/agencies/{agencyId}/users")
    public List<AgencyUserResponse> getUsersByAgency(@PathVariable Long agencyId) {
        return agencyUserAdminService.getUsersByAgency(agencyId);
    }

    @GetMapping("/roles")
    public List<RoleReferenceResponse> getAvailableRoles() {
        return agencyUserAdminService.getAvailableRoles();
    }
}
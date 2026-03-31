package com.pnpe.backend.service;

import com.pnpe.backend.dto.admin.AgencyUserResponse;
import com.pnpe.backend.dto.admin.CreateAgencyUserRequest;
import com.pnpe.backend.dto.admin.RoleReferenceResponse;
import com.pnpe.backend.dto.admin.UpdateAgencyUserRequest;
import com.pnpe.backend.dto.admin.UserStatusUpdateRequest;

import java.util.List;

public interface AgencyUserAdminService {

    AgencyUserResponse createUserInAgency(Long agencyId, CreateAgencyUserRequest request);

    AgencyUserResponse updateUser(Long userId, UpdateAgencyUserRequest request);

    AgencyUserResponse updateUserStatus(Long userId, UserStatusUpdateRequest request);

    AgencyUserResponse getUserById(Long userId);

    List<AgencyUserResponse> getUsersByAgency(Long agencyId);

    List<RoleReferenceResponse> getAvailableRoles();
}
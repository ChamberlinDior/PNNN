package com.pnpe.backend.service;

import com.pnpe.backend.dto.admin.*;

import java.util.List;

public interface AgencyAdminService {

    AgencyResponse createAgency(CreateAgencyRequest request);

    AgencyResponse updateAgency(Long agencyId, UpdateAgencyRequest request);

    AgencyResponse updateAgencyStatus(Long agencyId, AgencyStatusUpdateRequest request);

    AgencyResponse getAgencyById(Long agencyId);

    List<AgencyResponse> getAllAgencies();

    List<DepartmentOptionResponse> getAgencyDepartments(Long agencyId);
}
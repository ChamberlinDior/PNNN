package com.pnpe.backend.service.impl;

import com.pnpe.backend.dto.admin.*;
import com.pnpe.backend.exception.ConflictException;
import com.pnpe.backend.exception.ResourceNotFoundException;
import com.pnpe.backend.model.Agency;
import com.pnpe.backend.model.Department;
import com.pnpe.backend.repository.AgencyRepository;
import com.pnpe.backend.repository.DepartmentRepository;
import com.pnpe.backend.repository.UserRepository;
import com.pnpe.backend.service.AgencyAdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AgencyAdminServiceImpl implements AgencyAdminService {

    private final AgencyRepository agencyRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    public AgencyAdminServiceImpl(AgencyRepository agencyRepository,
                                  DepartmentRepository departmentRepository,
                                  UserRepository userRepository) {
        this.agencyRepository = agencyRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AgencyResponse createAgency(CreateAgencyRequest request) {
        String normalizedCode = normalize(request.code());
        String normalizedName = normalize(request.name());

        if (agencyRepository.existsByCodeIgnoreCase(normalizedCode)) {
            throw new ConflictException("Une agence avec ce code existe déjà");
        }

        if (agencyRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new ConflictException("Une agence avec ce nom existe déjà");
        }

        Agency agency = new Agency();
        agency.setCode(normalizedCode);
        agency.setName(normalizedName);
        agency.setCity(trimToNull(request.city()));
        agency.setProvince(trimToNull(request.province()));
        agency.setAddress(trimToNull(request.address()));
        agency.setHeadquarters(request.headquarters() != null ? request.headquarters() : Boolean.FALSE);
        agency.setActive(request.active() != null ? request.active() : Boolean.TRUE);

        Agency saved = agencyRepository.save(agency);
        return toResponse(saved);
    }

    @Override
    public AgencyResponse updateAgency(Long agencyId, UpdateAgencyRequest request) {
        Agency agency = agencyRepository.findById(agencyId)
                .orElseThrow(() -> new ResourceNotFoundException("Agence introuvable"));

        String normalizedCode = normalize(request.code());
        String normalizedName = normalize(request.name());

        agencyRepository.findByCodeIgnoreCase(normalizedCode)
                .filter(existing -> !existing.getId().equals(agencyId))
                .ifPresent(existing -> {
                    throw new ConflictException("Une autre agence utilise déjà ce code");
                });

        agencyRepository.findByNameIgnoreCase(normalizedName)
                .filter(existing -> !existing.getId().equals(agencyId))
                .ifPresent(existing -> {
                    throw new ConflictException("Une autre agence utilise déjà ce nom");
                });

        agency.setCode(normalizedCode);
        agency.setName(normalizedName);
        agency.setCity(trimToNull(request.city()));
        agency.setProvince(trimToNull(request.province()));
        agency.setAddress(trimToNull(request.address()));
        agency.setHeadquarters(request.headquarters() != null ? request.headquarters() : Boolean.FALSE);
        agency.setActive(request.active() != null ? request.active() : Boolean.TRUE);

        Agency saved = agencyRepository.save(agency);
        return toResponse(saved);
    }

    @Override
    public AgencyResponse updateAgencyStatus(Long agencyId, AgencyStatusUpdateRequest request) {
        Agency agency = agencyRepository.findById(agencyId)
                .orElseThrow(() -> new ResourceNotFoundException("Agence introuvable"));

        agency.setActive(request.active());
        Agency saved = agencyRepository.save(agency);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AgencyResponse getAgencyById(Long agencyId) {
        Agency agency = agencyRepository.findById(agencyId)
                .orElseThrow(() -> new ResourceNotFoundException("Agence introuvable"));
        return toResponse(agency);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgencyResponse> getAllAgencies() {
        return agencyRepository.findAllByOrderByHeadquartersDescNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentOptionResponse> getAgencyDepartments(Long agencyId) {
        if (!agencyRepository.existsById(agencyId)) {
            throw new ResourceNotFoundException("Agence introuvable");
        }

        return departmentRepository.findByAgencyIdOrderByNameAsc(agencyId)
                .stream()
                .map(this::toDepartmentResponse)
                .toList();
    }

    private AgencyResponse toResponse(Agency agency) {
        long usersCount = userRepository.countByAgencyId(agency.getId());
        long departmentsCount = departmentRepository.countByAgencyId(agency.getId());

        return new AgencyResponse(
                agency.getId(),
                agency.getCode(),
                agency.getName(),
                agency.getCity(),
                agency.getProvince(),
                agency.getAddress(),
                agency.getHeadquarters(),
                agency.getActive(),
                usersCount,
                departmentsCount
        );
    }

    private DepartmentOptionResponse toDepartmentResponse(Department department) {
        return new DepartmentOptionResponse(
                department.getId(),
                department.getName(),
                department.getType() != null ? department.getType().name() : null
        );
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
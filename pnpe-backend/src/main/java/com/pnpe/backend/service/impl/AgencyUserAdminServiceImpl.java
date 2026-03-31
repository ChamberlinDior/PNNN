package com.pnpe.backend.service.impl;

import com.pnpe.backend.dto.admin.*;
import com.pnpe.backend.exception.ConflictException;
import com.pnpe.backend.exception.ResourceNotFoundException;
import com.pnpe.backend.model.*;
import com.pnpe.backend.model.enums.RoleName;
import com.pnpe.backend.model.enums.UserStatus;
import com.pnpe.backend.repository.*;
import com.pnpe.backend.service.AgencyUserAdminService;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AgencyUserAdminServiceImpl implements AgencyUserAdminService {

    private final AgencyRepository agencyRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AgentProfileRepository agentProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public AgencyUserAdminServiceImpl(AgencyRepository agencyRepository,
                                      DepartmentRepository departmentRepository,
                                      UserRepository userRepository,
                                      RoleRepository roleRepository,
                                      AgentProfileRepository agentProfileRepository,
                                      PasswordEncoder passwordEncoder) {
        this.agencyRepository = agencyRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.agentProfileRepository = agentProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AgencyUserResponse createUserInAgency(Long agencyId, CreateAgencyUserRequest request) {
        Agency agency = agencyRepository.findById(agencyId)
                .orElseThrow(() -> new ResourceNotFoundException("Agence introuvable"));

        String normalizedEmail = normalizeEmail(request.email());
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ConflictException("Un utilisateur avec cet email existe déjà");
        }

        Role role = roleRepository.findByName(request.roleName())
                .orElseThrow(() -> new ResourceNotFoundException("Rôle introuvable"));

        Department department = resolveDepartment(agencyId, request.departmentId());

        User user = new User();
        user.setFirstName(normalizeText(request.firstName()));
        user.setLastName(normalizeText(request.lastName()));
        user.setEmail(normalizedEmail);
        user.setPhone(trimToNull(request.phone()));
        user.setProfilePhotoUrl(trimToNull(request.profilePhotoUrl()));
        user.setJobTitle(trimToNull(request.jobTitle()));
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(role);
        user.setAgency(agency);
        user.setDepartment(department);
        user.setStatus(request.status() != null ? request.status() : UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);

        AgentProfile agentProfile = maybeCreateAgentProfile(savedUser,
                request.createAgentProfile(),
                request.agentCode(),
                request.monthlyTargetInsertions(),
                request.monthlyTargetInterviews(),
                request.counselor(),
                request.specialty());

        return toResponse(savedUser, agentProfile);
    }

    @Override
    public AgencyUserResponse updateUser(Long userId, UpdateAgencyUserRequest request) {
        User user = userRepository.findDetailedById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        if (request.firstName() != null) {
            user.setFirstName(normalizeText(request.firstName()));
        }
        if (request.lastName() != null) {
            user.setLastName(normalizeText(request.lastName()));
        }
        if (request.email() != null) {
            String normalizedEmail = normalizeEmail(request.email());
            userRepository.findByEmailIgnoreCase(normalizedEmail)
                    .filter(existing -> !existing.getId().equals(userId))
                    .ifPresent(existing -> {
                        throw new ConflictException("Un autre utilisateur utilise déjà cet email");
                    });
            user.setEmail(normalizedEmail);
        }
        if (request.phone() != null) {
            user.setPhone(trimToNull(request.phone()));
        }
        if (request.profilePhotoUrl() != null) {
            user.setProfilePhotoUrl(trimToNull(request.profilePhotoUrl()));
        }
        if (request.jobTitle() != null) {
            user.setJobTitle(trimToNull(request.jobTitle()));
        }
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        if (request.roleName() != null) {
            Role role = roleRepository.findByName(request.roleName())
                    .orElseThrow(() -> new ResourceNotFoundException("Rôle introuvable"));
            user.setRole(role);
        }
        if (request.status() != null) {
            user.setStatus(request.status());
        }
        if (request.departmentId() != null) {
            Long agencyId = user.getAgency() != null ? user.getAgency().getId() : null;
            if (agencyId == null) {
                throw new ResourceNotFoundException("L'utilisateur n'est rattaché à aucune agence");
            }
            user.setDepartment(resolveDepartment(agencyId, request.departmentId()));
        }

        User savedUser = userRepository.save(user);

        AgentProfile agentProfile = synchronizeAgentProfile(savedUser,
                request.createAgentProfile(),
                request.agentCode(),
                request.monthlyTargetInsertions(),
                request.monthlyTargetInterviews(),
                request.counselor(),
                request.specialty());

        return toResponse(savedUser, agentProfile);
    }

    @Override
    public AgencyUserResponse updateUserStatus(Long userId, UserStatusUpdateRequest request) {
        User user = userRepository.findDetailedById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        user.setStatus(request.status());
        User saved = userRepository.save(user);
        AgentProfile agentProfile = agentProfileRepository.findByUserId(saved.getId()).orElse(null);

        return toResponse(saved, agentProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public AgencyUserResponse getUserById(Long userId) {
        User user = userRepository.findDetailedById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        AgentProfile agentProfile = agentProfileRepository.findByUserId(user.getId()).orElse(null);
        return toResponse(user, agentProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgencyUserResponse> getUsersByAgency(Long agencyId) {
        if (!agencyRepository.existsById(agencyId)) {
            throw new ResourceNotFoundException("Agence introuvable");
        }

        return userRepository.findByAgencyIdOrderByLastNameAscFirstNameAsc(agencyId)
                .stream()
                .map(user -> {
                    AgentProfile agentProfile = agentProfileRepository.findByUserId(user.getId()).orElse(null);
                    return toResponse(user, agentProfile);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleReferenceResponse> getAvailableRoles() {
        return roleRepository.findAll(Sort.by(Sort.Direction.ASC, "label"))
                .stream()
                .map(role -> new RoleReferenceResponse(
                        role.getId(),
                        role.getName() != null ? role.getName().name() : null,
                        role.getLabel()
                ))
                .toList();
    }

    private Department resolveDepartment(Long agencyId, Long departmentId) {
        if (departmentId == null) {
            return null;
        }
        return departmentRepository.findByIdAndAgencyId(departmentId, agencyId)
                .orElseThrow(() -> new ResourceNotFoundException("Le département n'appartient pas à cette agence"));
    }

    private AgentProfile maybeCreateAgentProfile(User user,
                                                 Boolean createAgentProfile,
                                                 String agentCode,
                                                 Integer monthlyTargetInsertions,
                                                 Integer monthlyTargetInterviews,
                                                 Boolean counselor,
                                                 String specialty) {
        boolean shouldCreate = Boolean.TRUE.equals(createAgentProfile);
        if (!shouldCreate) {
            return null;
        }

        if (agentCode == null || agentCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Le code agent est obligatoire pour créer un profil agent");
        }

        String normalizedAgentCode = agentCode.trim();
        if (agentProfileRepository.existsByAgentCodeIgnoreCase(normalizedAgentCode)) {
            throw new ConflictException("Ce code agent existe déjà");
        }

        AgentProfile profile = new AgentProfile();
        profile.setUser(user);
        profile.setAgentCode(normalizedAgentCode);
        profile.setMonthlyTargetInsertions(monthlyTargetInsertions != null ? monthlyTargetInsertions : 3);
        profile.setMonthlyTargetInterviews(monthlyTargetInterviews != null ? monthlyTargetInterviews : 20);
        profile.setCounselor(counselor != null ? counselor : Boolean.TRUE);
        profile.setSpecialty(trimToNull(specialty));

        return agentProfileRepository.save(profile);
    }

    private AgentProfile synchronizeAgentProfile(User user,
                                                 Boolean createAgentProfile,
                                                 String agentCode,
                                                 Integer monthlyTargetInsertions,
                                                 Integer monthlyTargetInterviews,
                                                 Boolean counselor,
                                                 String specialty) {
        AgentProfile existingProfile = agentProfileRepository.findByUserId(user.getId()).orElse(null);

        if (Boolean.TRUE.equals(createAgentProfile) && existingProfile == null) {
            return maybeCreateAgentProfile(user,
                    true,
                    agentCode,
                    monthlyTargetInsertions,
                    monthlyTargetInterviews,
                    counselor,
                    specialty);
        }

        if (existingProfile == null) {
            return null;
        }

        if (agentCode != null && !agentCode.isBlank()) {
            String normalizedAgentCode = agentCode.trim();
            if (!normalizedAgentCode.equalsIgnoreCase(existingProfile.getAgentCode())
                    && agentProfileRepository.existsByAgentCodeIgnoreCase(normalizedAgentCode)) {
                throw new ConflictException("Ce code agent existe déjà");
            }
            existingProfile.setAgentCode(normalizedAgentCode);
        }

        if (monthlyTargetInsertions != null) {
            existingProfile.setMonthlyTargetInsertions(monthlyTargetInsertions);
        }
        if (monthlyTargetInterviews != null) {
            existingProfile.setMonthlyTargetInterviews(monthlyTargetInterviews);
        }
        if (counselor != null) {
            existingProfile.setCounselor(counselor);
        }
        if (specialty != null) {
            existingProfile.setSpecialty(trimToNull(specialty));
        }

        return agentProfileRepository.save(existingProfile);
    }

    private AgencyUserResponse toResponse(User user, AgentProfile agentProfile) {
        Role role = user.getRole();
        Agency agency = user.getAgency();
        Department department = user.getDepartment();

        return new AgencyUserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getFullName() != null ? user.getFullName().trim() : null,
                user.getEmail(),
                user.getPhone(),
                user.getProfilePhotoUrl(),
                user.getJobTitle(),
                user.getStatus() != null ? user.getStatus().name() : null,
                role != null ? role.getId() : null,
                role != null && role.getName() != null ? role.getName().name() : null,
                role != null ? role.getLabel() : null,
                agency != null ? agency.getId() : null,
                agency != null ? agency.getCode() : null,
                agency != null ? agency.getName() : null,
                department != null ? department.getId() : null,
                department != null ? department.getName() : null,
                agentProfile != null ? agentProfile.getId() : null,
                agentProfile != null ? agentProfile.getAgentCode() : null,
                agentProfile != null ? agentProfile.getCounselor() : null,
                agentProfile != null ? agentProfile.getMonthlyTargetInsertions() : null,
                agentProfile != null ? agentProfile.getMonthlyTargetInterviews() : null,
                agentProfile != null ? agentProfile.getSpecialty() : null
        );
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private String normalizeText(String value) {
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
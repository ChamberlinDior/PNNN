package com.pnpe.backend.service.impl;

import com.pnpe.backend.dto.AuthRequest;
import com.pnpe.backend.dto.AuthResponse;
import com.pnpe.backend.model.AgentProfile;
import com.pnpe.backend.model.Department;
import com.pnpe.backend.model.User;
import com.pnpe.backend.repository.AgentProfileRepository;
import com.pnpe.backend.repository.UserRepository;
import com.pnpe.backend.security.JwtService;
import com.pnpe.backend.service.AuthService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AgentProfileRepository agentProfileRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository,
                           AgentProfileRepository agentProfileRepository,
                           JwtService jwtService,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.agentProfileRepository = agentProfileRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public AuthResponse login(AuthRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        User user = userRepository.findWithRoleByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        if (user.getPassword() == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Mot de passe incorrect");
        }

        String role = "ROLE_USER";
        if (user.getRole() != null && user.getRole().getName() != null) {
            role = user.getRole().getName().name();
        }

        AgentProfile agentProfile = agentProfileRepository.findByUserId(user.getId()).orElse(null);
        Long agentProfileId = agentProfile != null ? agentProfile.getId() : null;

        Long agencyId = null;
        String agencyName = null;
        Long departmentId = null;
        String departmentName = null;

        if (user.getAgency() != null) {
            agencyId = user.getAgency().getId();
            agencyName = user.getAgency().getName();
        }

        Department department = user.getDepartment();
        if (department != null) {
            departmentId = department.getId();
            departmentName = department.getName();
        }

        String token = jwtService.generateToken(user.getEmail(), role);

        return new AuthResponse(
                token,
                "Bearer",
                user.getId(),
                agentProfileId,
                ((user.getFirstName() != null ? user.getFirstName() : "") + " " +
                        (user.getLastName() != null ? user.getLastName() : "")).trim(),
                user.getEmail(),
                role,
                agencyId,
                agencyName,
                departmentId,
                departmentName
        );
    }
}
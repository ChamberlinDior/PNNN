package com.pnpe.backend.controller;

import com.pnpe.backend.dto.CounselorReferenceResponse;
import com.pnpe.backend.model.Agency;
import com.pnpe.backend.model.AgentProfile;
import com.pnpe.backend.model.Department;
import com.pnpe.backend.repository.AgencyRepository;
import com.pnpe.backend.repository.AgentProfileRepository;
import com.pnpe.backend.repository.DepartmentRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/references")
@CrossOrigin(origins = "*")
public class ReferenceController {

    private final AgencyRepository agencyRepository;
    private final DepartmentRepository departmentRepository;
    private final AgentProfileRepository agentProfileRepository;

    public ReferenceController(AgencyRepository agencyRepository,
                               DepartmentRepository departmentRepository,
                               AgentProfileRepository agentProfileRepository) {
        this.agencyRepository = agencyRepository;
        this.departmentRepository = departmentRepository;
        this.agentProfileRepository = agentProfileRepository;
    }

    @GetMapping("/agencies")
    public List<Agency> agencies() {
        return agencyRepository.findAll();
    }

    @GetMapping("/departments")
    public List<Department> departments() {
        return departmentRepository.findAll();
    }

    @GetMapping("/counselors")
    public List<CounselorReferenceResponse> counselors() {
        return agentProfileRepository.findByCounselorTrueOrderByUserLastNameAscUserFirstNameAsc()
                .stream()
                .map(this::toCounselorReferenceResponse)
                .toList();
    }

    private CounselorReferenceResponse toCounselorReferenceResponse(AgentProfile profile) {
        Long userId = profile.getUser() != null ? profile.getUser().getId() : null;
        String fullName = null;
        String email = null;

        if (profile.getUser() != null) {
            String firstName = profile.getUser().getFirstName() != null ? profile.getUser().getFirstName() : "";
            String lastName = profile.getUser().getLastName() != null ? profile.getUser().getLastName() : "";
            fullName = (firstName + " " + lastName).trim();
            email = profile.getUser().getEmail();
        }

        return new CounselorReferenceResponse(
                profile.getId(),
                profile.getAgentCode(),
                profile.getSpecialty(),
                userId,
                fullName,
                email
        );
    }
}
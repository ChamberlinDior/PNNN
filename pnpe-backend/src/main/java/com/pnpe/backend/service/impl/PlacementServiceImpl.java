package com.pnpe.backend.service.impl;

import com.pnpe.backend.dto.PlacementRequest;
import com.pnpe.backend.dto.PlacementResponse;
import com.pnpe.backend.exception.ResourceNotFoundException;
import com.pnpe.backend.model.AgentProfile;
import com.pnpe.backend.model.Company;
import com.pnpe.backend.model.EmploymentPlacement;
import com.pnpe.backend.model.Interview;
import com.pnpe.backend.model.JobSeeker;
import com.pnpe.backend.model.User;
import com.pnpe.backend.model.enums.JobSeekerStatus;
import com.pnpe.backend.repository.AgentProfileRepository;
import com.pnpe.backend.repository.CompanyRepository;
import com.pnpe.backend.repository.EmploymentPlacementRepository;
import com.pnpe.backend.repository.InterviewRepository;
import com.pnpe.backend.repository.JobSeekerRepository;
import com.pnpe.backend.service.PlacementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PlacementServiceImpl implements PlacementService {

    private final EmploymentPlacementRepository placementRepository;
    private final JobSeekerRepository jobSeekerRepository;
    private final CompanyRepository companyRepository;
    private final AgentProfileRepository agentProfileRepository;
    private final InterviewRepository interviewRepository;

    public PlacementServiceImpl(EmploymentPlacementRepository placementRepository,
                                JobSeekerRepository jobSeekerRepository,
                                CompanyRepository companyRepository,
                                AgentProfileRepository agentProfileRepository,
                                InterviewRepository interviewRepository) {
        this.placementRepository = placementRepository;
        this.jobSeekerRepository = jobSeekerRepository;
        this.companyRepository = companyRepository;
        this.agentProfileRepository = agentProfileRepository;
        this.interviewRepository = interviewRepository;
    }

    @Override
    public PlacementResponse create(PlacementRequest request) {
        JobSeeker seeker = jobSeekerRepository.findById(request.jobSeekerId())
                .orElseThrow(() -> new ResourceNotFoundException("Demandeur introuvable"));

        Company company = companyRepository.findById(request.companyId())
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise introuvable"));

        AgentProfile counselor = agentProfileRepository.findById(request.counselorId())
                .orElseThrow(() -> new ResourceNotFoundException("Conseiller introuvable"));

        EmploymentPlacement placement = new EmploymentPlacement();
        placement.setJobSeeker(seeker);
        placement.setCompany(company);
        placement.setCounselor(counselor);
        placement.setPositionTitle(request.positionTitle());
        placement.setStartDate(request.startDate());
        placement.setContractType(request.contractType());
        placement.setEmploymentStatus(request.employmentStatus());
        placement.setNotes(request.notes());

        if (request.interviewId() != null) {
            Interview interview = interviewRepository.findById(request.interviewId())
                    .orElseThrow(() -> new ResourceNotFoundException("Entretien introuvable"));
            placement.setInterview(interview);
        }

        seeker.setStatus(JobSeekerStatus.INSERTED_CONFIRMED);
        jobSeekerRepository.save(seeker);

        EmploymentPlacement savedPlacement = placementRepository.save(placement);

        return toResponse(savedPlacement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlacementResponse> findAll() {
        return placementRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private PlacementResponse toResponse(EmploymentPlacement placement) {
        return new PlacementResponse(
                placement.getId(),
                buildJobSeekerName(placement.getJobSeeker()),
                buildCompanyName(placement.getCompany()),
                buildCounselorName(placement.getCounselor()),
                placement.getPositionTitle(),
                placement.getStartDate(),
                placement.getContractType(),
                placement.getEmploymentStatus(),
                placement.getNotes()
        );
    }

    private String buildJobSeekerName(JobSeeker jobSeeker) {
        if (jobSeeker == null) {
            return "Demandeur inconnu";
        }

        String firstName = jobSeeker.getFirstName() != null ? jobSeeker.getFirstName().trim() : "";
        String lastName = jobSeeker.getLastName() != null ? jobSeeker.getLastName().trim() : "";

        String fullName = (firstName + " " + lastName).trim();
        return fullName.isBlank() ? "Demandeur inconnu" : fullName;
    }

    private String buildCompanyName(Company company) {
        if (company == null || company.getName() == null || company.getName().trim().isBlank()) {
            return "Entreprise inconnue";
        }
        return company.getName().trim();
    }

    private String buildCounselorName(AgentProfile counselor) {
        if (counselor == null) {
            return "Conseiller inconnu";
        }

        User user = counselor.getUser();
        if (user == null || user.getFullName() == null || user.getFullName().trim().isBlank()) {
            return "Conseiller inconnu";
        }

        return user.getFullName().trim();
    }
}
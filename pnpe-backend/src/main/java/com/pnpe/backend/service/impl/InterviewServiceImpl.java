package com.pnpe.backend.service.impl;

import com.pnpe.backend.dto.InterviewRequest;
import com.pnpe.backend.dto.InterviewResponse;
import com.pnpe.backend.dto.InterviewUpdateRequest;
import com.pnpe.backend.exception.ResourceNotFoundException;
import com.pnpe.backend.model.AgentProfile;
import com.pnpe.backend.model.Company;
import com.pnpe.backend.model.Interview;
import com.pnpe.backend.model.JobSeeker;
import com.pnpe.backend.model.User;
import com.pnpe.backend.model.enums.InterviewStatus;
import com.pnpe.backend.model.enums.JobSeekerStatus;
import com.pnpe.backend.repository.AgentProfileRepository;
import com.pnpe.backend.repository.CompanyRepository;
import com.pnpe.backend.repository.InterviewRepository;
import com.pnpe.backend.repository.JobSeekerRepository;
import com.pnpe.backend.service.InterviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class InterviewServiceImpl implements InterviewService {

    private final InterviewRepository interviewRepository;
    private final JobSeekerRepository jobSeekerRepository;
    private final CompanyRepository companyRepository;
    private final AgentProfileRepository agentProfileRepository;

    public InterviewServiceImpl(InterviewRepository interviewRepository,
                                JobSeekerRepository jobSeekerRepository,
                                CompanyRepository companyRepository,
                                AgentProfileRepository agentProfileRepository) {
        this.interviewRepository = interviewRepository;
        this.jobSeekerRepository = jobSeekerRepository;
        this.companyRepository = companyRepository;
        this.agentProfileRepository = agentProfileRepository;
    }

    @Override
    public InterviewResponse create(InterviewRequest request) {
        JobSeeker jobSeeker = jobSeekerRepository.findById(request.jobSeekerId())
                .orElseThrow(() -> new ResourceNotFoundException("Demandeur introuvable"));

        Company company = companyRepository.findById(request.companyId())
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise introuvable"));

        AgentProfile agentProfile = agentProfileRepository.findById(request.agentProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Conseiller introuvable"));

        Interview interview = new Interview();
        interview.setJobSeeker(jobSeeker);
        interview.setCompany(company);
        interview.setAgentProfile(agentProfile);
        interview.setJobTitle(request.jobTitle());
        interview.setInterviewDate(request.interviewDate());
        interview.setLocation(request.location());
        interview.setMode(request.mode());

        jobSeeker.setStatus(JobSeekerStatus.IN_PROCESS);
        jobSeekerRepository.save(jobSeeker);

        Interview savedInterview = interviewRepository.save(interview);

        return toResponse(savedInterview);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewResponse> findAll() {
        return interviewRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public InterviewResponse updateStatus(Long interviewId, InterviewUpdateRequest request) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Entretien introuvable"));

        interview.setStatus(request.status());
        interview.setFeedback(request.feedback());
        interview.setApplicantFeedback(request.applicantFeedback());
        interview.setCompanyFeedback(request.companyFeedback());
        interview.setNoShowReason(request.noShowReason());
        interview.setFollowUpDecision(request.followUpDecision());
        interview.setFollowUpAt(request.followUpAt());
        interview.setProposedContractType(request.proposedContractType());
        interview.setHiredAt(request.hiredAt());

        JobSeeker seeker = interview.getJobSeeker();

        if (request.status() == InterviewStatus.NO_SHOW) {
            int currentNoShow = seeker.getNoShowCount() == null ? 0 : seeker.getNoShowCount();
            seeker.setNoShowCount(currentNoShow + 1);

            if (seeker.getNoShowCount() >= 3) {
                seeker.setStatus(JobSeekerStatus.SUSPENDED);
            }
        } else if (request.status() == InterviewStatus.HIRED) {
            seeker.setStatus(JobSeekerStatus.INSERTED_PENDING_CONFIRMATION);
        } else if (request.status() == InterviewStatus.COMPLETED) {
            seeker.setStatus(JobSeekerStatus.IN_PROCESS);
        }

        jobSeekerRepository.save(seeker);

        Interview updatedInterview = interviewRepository.save(interview);

        return toResponse(updatedInterview);
    }

    private InterviewResponse toResponse(Interview interview) {
        String jobSeekerName = buildJobSeekerName(interview.getJobSeeker());
        String companyName = buildCompanyName(interview.getCompany());
        String agentName = buildAgentName(interview.getAgentProfile());

        return new InterviewResponse(
                interview.getId(),
                jobSeekerName,
                companyName,
                agentName,
                interview.getJobTitle(),
                interview.getInterviewDate(),
                interview.getLocation(),
                interview.getMode(),
                interview.getStatus(),
                interview.getFollowUpDecision()
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

    private String buildAgentName(AgentProfile agentProfile) {
        if (agentProfile == null) {
            return "Conseiller inconnu";
        }

        User user = agentProfile.getUser();
        if (user == null || user.getFullName() == null || user.getFullName().trim().isBlank()) {
            return "Conseiller inconnu";
        }

        return user.getFullName().trim();
    }
}
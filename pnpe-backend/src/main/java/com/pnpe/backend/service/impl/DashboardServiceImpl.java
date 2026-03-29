package com.pnpe.backend.service.impl;

import com.pnpe.backend.dto.DashboardResponse;
import com.pnpe.backend.model.Agency;
import com.pnpe.backend.model.enums.InterviewStatus;
import com.pnpe.backend.repository.*;
import com.pnpe.backend.service.DashboardService;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final AgentProfileRepository agentProfileRepository;
    private final JobSeekerRepository jobSeekerRepository;
    private final CompanyRepository companyRepository;
    private final InterviewRepository interviewRepository;
    private final PreRegistrationRepository preRegistrationRepository;
    private final EmploymentPlacementRepository employmentPlacementRepository;
    private final AgencyRepository agencyRepository;

    public DashboardServiceImpl(UserRepository userRepository,
                                AgentProfileRepository agentProfileRepository,
                                JobSeekerRepository jobSeekerRepository,
                                CompanyRepository companyRepository,
                                InterviewRepository interviewRepository,
                                PreRegistrationRepository preRegistrationRepository,
                                EmploymentPlacementRepository employmentPlacementRepository,
                                AgencyRepository agencyRepository) {
        this.userRepository = userRepository;
        this.agentProfileRepository = agentProfileRepository;
        this.jobSeekerRepository = jobSeekerRepository;
        this.companyRepository = companyRepository;
        this.interviewRepository = interviewRepository;
        this.preRegistrationRepository = preRegistrationRepository;
        this.employmentPlacementRepository = employmentPlacementRepository;
        this.agencyRepository = agencyRepository;
    }

    @Override
    public DashboardResponse getSummary() {
        return new DashboardResponse(
                userRepository.count(),
                agentProfileRepository.count(),
                jobSeekerRepository.count(),
                preRegistrationRepository.count(),
                companyRepository.count(),
                interviewRepository.count(),
                interviewRepository.countByStatus(InterviewStatus.SCHEDULED),
                interviewRepository.countByStatus(InterviewStatus.COMPLETED),
                employmentPlacementRepository.count(),
                agencyRepository.findAll().stream().map(this::toAgencyItem).toList()
        );
    }

    private DashboardResponse.AgencyDashboardItem toAgencyItem(Agency agency) {
        long counselors = agentProfileRepository.findAll().stream().filter(a -> a.getUser() != null && a.getUser().getAgency() != null && a.getUser().getAgency().getId().equals(agency.getId())).count();
        long preRegs = preRegistrationRepository.findAll().stream().filter(pr -> pr.getAgency() != null && pr.getAgency().getId().equals(agency.getId())).count();
        return new DashboardResponse.AgencyDashboardItem(
                agency.getId(),
                agency.getCode(),
                agency.getName(),
                counselors,
                jobSeekerRepository.countByAgencyId(agency.getId()),
                preRegs
        );
    }
}

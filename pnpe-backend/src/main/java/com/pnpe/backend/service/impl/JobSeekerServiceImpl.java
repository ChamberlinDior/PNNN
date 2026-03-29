package com.pnpe.backend.service.impl;

import com.pnpe.backend.dto.JobSeekerRequest;
import com.pnpe.backend.dto.JobSeekerResponse;
import com.pnpe.backend.exception.ResourceNotFoundException;
import com.pnpe.backend.model.Agency;
import com.pnpe.backend.model.AgentProfile;
import com.pnpe.backend.model.JobSeeker;
import com.pnpe.backend.model.PreRegistration;
import com.pnpe.backend.model.enums.JobSeekerStatus;
import com.pnpe.backend.model.enums.PreRegistrationStatus;
import com.pnpe.backend.repository.AgencyRepository;
import com.pnpe.backend.repository.AgentProfileRepository;
import com.pnpe.backend.repository.JobSeekerRepository;
import com.pnpe.backend.repository.PreRegistrationRepository;
import com.pnpe.backend.service.JobSeekerService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobSeekerServiceImpl implements JobSeekerService {

    private static final String DOSSIER_PREFIX = "DEM-";
    private static final long DOSSIER_START = 1001L;

    private static final String OPEN_PREFIX = "OPEN-";
    private static final long OPEN_START = 50001L;

    private static final int MAX_GENERATION_ATTEMPTS = 10;

    private final JobSeekerRepository jobSeekerRepository;
    private final AgencyRepository agencyRepository;
    private final AgentProfileRepository agentProfileRepository;
    private final PreRegistrationRepository preRegistrationRepository;

    public JobSeekerServiceImpl(JobSeekerRepository jobSeekerRepository,
                                AgencyRepository agencyRepository,
                                AgentProfileRepository agentProfileRepository,
                                PreRegistrationRepository preRegistrationRepository,
                                SequenceGenerator sequenceGenerator) {
        this.jobSeekerRepository = jobSeekerRepository;
        this.agencyRepository = agencyRepository;
        this.agentProfileRepository = agentProfileRepository;
        this.preRegistrationRepository = preRegistrationRepository;
    }

    @Override
    @Transactional
    public JobSeekerResponse create(JobSeekerRequest request) {
        if (request.preRegistrationId() != null
                && jobSeekerRepository.existsByPreRegistrationId(request.preRegistrationId())) {
            throw new IllegalStateException("Cette pré-inscription a déjà été convertie en demandeur d'emploi.");
        }

        for (int attempt = 0; attempt < MAX_GENERATION_ATTEMPTS; attempt++) {
            JobSeeker seeker = buildJobSeeker(request);
            seeker.setDossierNumber(generateNextDossierNumber());
            seeker.setOpenNumber(generateNextOpenNumber());

            try {
                JobSeeker saved = jobSeekerRepository.saveAndFlush(seeker);

                if (saved.getPreRegistration() != null) {
                    PreRegistration preRegistration = saved.getPreRegistration();
                    preRegistration.setStatus(PreRegistrationStatus.CONVERTED_TO_JOB_SEEKER);
                    preRegistrationRepository.save(preRegistration);
                }

                return toResponse(saved);
            } catch (DataIntegrityViolationException ex) {
                if (!isDuplicateJobSeekerNumberException(ex)) {
                    throw ex;
                }
            }
        }

        throw new IllegalStateException(
                "Impossible de générer un numéro unique de dossier demandeur d'emploi. Veuillez réessayer."
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobSeekerResponse> findAll() {
        return jobSeekerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobSeekerResponse> search(String keyword) {
        return jobSeekerRepository
                .findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrOpenNumberContainingIgnoreCase(
                        keyword,
                        keyword,
                        keyword
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public JobSeekerResponse findById(Long id) {
        return toResponse(
                jobSeekerRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Demandeur introuvable"))
        );
    }

    private JobSeeker buildJobSeeker(JobSeekerRequest request) {
        JobSeeker seeker = new JobSeeker();

        seeker.setFirstName(request.firstName());
        seeker.setLastName(request.lastName());
        seeker.setGender(request.gender());
        seeker.setDateOfBirth(request.dateOfBirth());
        seeker.setPhone(request.phone());
        seeker.setEmail(request.email());
        seeker.setCity(request.city());
        seeker.setAddress(request.address());
        seeker.setEducationLevel(request.educationLevel());
        seeker.setPrimarySkill(request.primarySkill());
        seeker.setProjectSummary(request.projectSummary());
        seeker.setActionPlanSummary(request.actionPlanSummary());
        seeker.setSelfRegistered(Boolean.TRUE.equals(request.selfRegistered()));
        seeker.setStatus(JobSeekerStatus.ACTIVE);
        seeker.setRegistrationValidatedAt(LocalDateTime.now());

        if (request.agencyId() != null) {
            Agency agency = agencyRepository.findById(request.agencyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Agence introuvable"));
            seeker.setAgency(agency);
        }

        if (request.assignedAgentId() != null) {
            AgentProfile agentProfile = agentProfileRepository.findById(request.assignedAgentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Conseiller introuvable"));
            seeker.setAssignedAgent(agentProfile);

            if (seeker.getAgency() == null && agentProfile.getUser() != null) {
                seeker.setAgency(agentProfile.getUser().getAgency());
            }
        }

        if (request.preRegistrationId() != null) {
            PreRegistration preRegistration = preRegistrationRepository.findById(request.preRegistrationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Pré-inscription introuvable"));

            seeker.setPreRegistration(preRegistration);

            if (seeker.getAgency() == null) {
                seeker.setAgency(preRegistration.getAgency());
            }

            if (seeker.getAssignedAgent() == null) {
                seeker.setAssignedAgent(preRegistration.getReferredCounselor());
            }

            if (seeker.getAgency() == null
                    && preRegistration.getReferredCounselor() != null
                    && preRegistration.getReferredCounselor().getUser() != null) {
                seeker.setAgency(preRegistration.getReferredCounselor().getUser().getAgency());
            }
        }

        return seeker;
    }

    private synchronized String generateNextDossierNumber() {
        Long maxValue = jobSeekerRepository.findMaxDossierNumberValue();
        long nextValue = (maxValue == null || maxValue < DOSSIER_START)
                ? DOSSIER_START
                : maxValue + 1;

        String candidate = DOSSIER_PREFIX + nextValue;

        while (jobSeekerRepository.existsByDossierNumber(candidate)) {
            nextValue++;
            candidate = DOSSIER_PREFIX + nextValue;
        }

        return candidate;
    }

    private synchronized String generateNextOpenNumber() {
        Long maxValue = jobSeekerRepository.findMaxOpenNumberValue();
        long nextValue = (maxValue == null || maxValue < OPEN_START)
                ? OPEN_START
                : maxValue + 1;

        String candidate = OPEN_PREFIX + nextValue;

        while (jobSeekerRepository.existsByOpenNumber(candidate)) {
            nextValue++;
            candidate = OPEN_PREFIX + nextValue;
        }

        return candidate;
    }

    private boolean isDuplicateJobSeekerNumberException(Throwable ex) {
        Throwable current = ex;
        while (current != null) {
            String message = current.getMessage();

            if (message != null && message.contains("Duplicate entry") && message.contains("dossier_number")) {
                return true;
            }

            if (message != null && message.contains("Duplicate entry") && message.contains("open_number")) {
                return true;
            }

            if (message != null && message.contains("Duplicate entry") && message.contains("job_seekers")) {
                return true;
            }

            current = current.getCause();
        }
        return false;
    }

    private JobSeekerResponse toResponse(JobSeeker seeker) {
        String agencyName = seeker.getAgency() != null ? seeker.getAgency().getName() : null;

        String agentName = null;
        if (seeker.getAssignedAgent() != null && seeker.getAssignedAgent().getUser() != null) {
            String fullName = seeker.getAssignedAgent().getUser().getFullName();
            if (fullName != null) {
                agentName = fullName.trim();
            }
        }

        return new JobSeekerResponse(
                seeker.getId(),
                seeker.getDossierNumber(),
                seeker.getOpenNumber(),
                seeker.getFirstName(),
                seeker.getLastName(),
                seeker.getPhone(),
                seeker.getEmail(),
                seeker.getCity(),
                seeker.getPrimarySkill(),
                seeker.getStatus(),
                agencyName,
                agentName,
                seeker.getNoShowCount(),
                seeker.getSelfRegistered()
        );
    }
}
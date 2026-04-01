package com.pnpe.backend.service.impl;

import com.pnpe.backend.dto.JobSeekerRequest;
import com.pnpe.backend.dto.PnpeCardResponse;
import com.pnpe.backend.dto.PreRegistrationRequest;
import com.pnpe.backend.dto.PreRegistrationResponse;
import com.pnpe.backend.exception.ResourceNotFoundException;
import com.pnpe.backend.model.Agency;
import com.pnpe.backend.model.AgentProfile;
import com.pnpe.backend.model.JobSeekerDocument;
import com.pnpe.backend.model.PreRegistration;
import com.pnpe.backend.model.User;
import com.pnpe.backend.model.enums.PreRegistrationStatus;
import com.pnpe.backend.model.enums.RoleName;
import com.pnpe.backend.repository.AgencyRepository;
import com.pnpe.backend.repository.AgentProfileRepository;
import com.pnpe.backend.repository.JobSeekerDocumentRepository;
import com.pnpe.backend.repository.PreRegistrationRepository;
import com.pnpe.backend.repository.UserRepository;
import com.pnpe.backend.service.JobSeekerService;
import com.pnpe.backend.service.PnpeCardService;
import com.pnpe.backend.service.PreRegistrationService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PreRegistrationServiceImpl implements PreRegistrationService {

    private static final String REQUEST_PREFIX = "PR-";
    private static final long REQUEST_START = 1001L;
    private static final int MAX_GENERATION_ATTEMPTS = 10;

    private final PreRegistrationRepository preRegistrationRepository;
    private final AgencyRepository agencyRepository;
    private final AgentProfileRepository agentProfileRepository;
    private final JobSeekerService jobSeekerService;
    private final UserRepository userRepository;
    private final JobSeekerDocumentRepository documentRepository;
    private final PnpeCardService pnpeCardService;

    public PreRegistrationServiceImpl(PreRegistrationRepository preRegistrationRepository,
                                      AgencyRepository agencyRepository,
                                      AgentProfileRepository agentProfileRepository,
                                      SequenceGenerator sequenceGenerator,
                                      JobSeekerService jobSeekerService,
                                      UserRepository userRepository,
                                      JobSeekerDocumentRepository documentRepository,
                                      PnpeCardService pnpeCardService) {
        this.preRegistrationRepository = preRegistrationRepository;
        this.agencyRepository = agencyRepository;
        this.agentProfileRepository = agentProfileRepository;
        this.jobSeekerService = jobSeekerService;
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
        this.pnpeCardService = pnpeCardService;
    }

    @Override
    @Transactional
    public PreRegistrationResponse create(PreRegistrationRequest request) {
        for (int attempt = 0; attempt < MAX_GENERATION_ATTEMPTS; attempt++) {
            PreRegistration preRegistration = buildPreRegistration(request);
            preRegistration.setRequestNumber(generateNextRequestNumber());

            try {
                PreRegistration saved = preRegistrationRepository.saveAndFlush(preRegistration);
                return toResponse(saved);
            } catch (DataIntegrityViolationException ex) {
                if (!isDuplicateRequestNumberException(ex)) {
                    throw ex;
                }
            }
        }

        throw new IllegalStateException(
                "Impossible de générer un numéro unique de pré-inscription. Veuillez réessayer."
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PreRegistrationResponse> findAll() {
        return preRegistrationRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PreRegistrationResponse validateDocumentsByScanner(Long preRegistrationId, Long scannerUserId) {
        PreRegistration preRegistration = preRegistrationRepository.findById(preRegistrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Pré-inscription introuvable"));

        User scanner = userRepository.findDetailedById(scannerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur scanner introuvable"));

        if (scanner.getRole() == null || scanner.getRole().getName() != RoleName.ROLE_POLE_SCAN) {
            throw new IllegalStateException("Seul un utilisateur du pôle scan peut valider la vérification des pièces.");
        }

        List<JobSeekerDocument> documents = documentRepository.findByPreRegistrationIdOrderByCreatedAtDesc(preRegistrationId);
        if (documents.isEmpty()) {
            throw new IllegalStateException("Aucune pièce n'a été chargée pour cette pré-inscription.");
        }

        for (JobSeekerDocument document : documents) {
            document.setVerified(true);
        }
        documentRepository.saveAll(documents);

        preRegistration.setDocumentsVerifiedByScan(true);
        preRegistration.setDocumentsVerifiedAt(LocalDateTime.now());
        preRegistration.setDocumentsVerifiedBy(scanner);
        preRegistration.setHasRequiredDocuments(true);

        PreRegistration saved = preRegistrationRepository.save(preRegistration);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public PreRegistrationResponse markReadyForCounselor(Long preRegistrationId, Long counselorId, Long scannerUserId) {
        PreRegistration preRegistration = preRegistrationRepository.findById(preRegistrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Pré-inscription introuvable"));

        AgentProfile counselor = agentProfileRepository.findById(counselorId)
                .orElseThrow(() -> new ResourceNotFoundException("Conseiller introuvable"));

        User scanner = userRepository.findDetailedById(scannerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur scanner introuvable"));

        if (scanner.getRole() == null || scanner.getRole().getName() != RoleName.ROLE_POLE_SCAN) {
            throw new IllegalStateException("Seul un utilisateur du pôle scan peut attribuer et transmettre un dossier.");
        }

        List<JobSeekerDocument> documents = documentRepository.findByPreRegistrationIdOrderByCreatedAtDesc(preRegistrationId);
        if (documents.isEmpty()) {
            throw new IllegalStateException("Aucune pièce n'a été chargée pour cette pré-inscription.");
        }

        for (JobSeekerDocument document : documents) {
            document.setVerified(true);
        }
        documentRepository.saveAll(documents);

        preRegistration.setDocumentsVerifiedByScan(true);
        preRegistration.setDocumentsVerifiedAt(LocalDateTime.now());
        preRegistration.setDocumentsVerifiedBy(scanner);
        preRegistration.setHasRequiredDocuments(true);

        preRegistration.setReferredCounselor(counselor);
        preRegistration.setStatus(PreRegistrationStatus.READY_FOR_COUNSELOR);

        Agency handlingAgency = extractAgencyFromAgent(counselor);
        if (handlingAgency != null) {
            preRegistration.setAgency(handlingAgency);
        }

        PreRegistration saved = preRegistrationRepository.save(preRegistration);

        // La carte est générée ici, au moment de "Attribuer / transmettre"
        pnpeCardService.createForPreRegistration(saved.getId(), scannerUserId);

        PreRegistration refreshed = preRegistrationRepository.findById(saved.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Pré-inscription introuvable après transmission scan"));

        return toResponse(refreshed);
    }

    @Override
    @Transactional
    public PreRegistrationResponse confirmCounselorConnection(Long preRegistrationId, Long counselorId) {
        PreRegistration preRegistration = preRegistrationRepository.findById(preRegistrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Pré-inscription introuvable"));

        AgentProfile counselor = agentProfileRepository.findById(counselorId)
                .orElseThrow(() -> new ResourceNotFoundException("Conseiller introuvable"));

        if (preRegistration.getStatus() != PreRegistrationStatus.READY_FOR_COUNSELOR) {
            throw new IllegalStateException(
                    "Le dossier doit être au statut READY_FOR_COUNSELOR avant la prise en charge conseiller."
            );
        }

        preRegistration.setReferredCounselor(counselor);
        preRegistration.setStatus(PreRegistrationStatus.VALIDATED);
        preRegistration.setCounselorAppointmentAt(LocalDateTime.now());

        Agency handlingAgency = extractAgencyFromAgent(counselor);
        if (handlingAgency != null) {
            preRegistration.setAgency(handlingAgency);
        }

        PreRegistration saved = preRegistrationRepository.save(preRegistration);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public PreRegistrationResponse validateAndConvert(Long preRegistrationId, Long counselorId) {
        PreRegistration preRegistration = preRegistrationRepository.findById(preRegistrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Pré-inscription introuvable"));

        AgentProfile counselor = agentProfileRepository.findById(counselorId)
                .orElseThrow(() -> new ResourceNotFoundException("Conseiller introuvable"));

        if (preRegistration.getStatus() == PreRegistrationStatus.CONVERTED_TO_JOB_SEEKER) {
            return toResponse(preRegistration);
        }

        if (preRegistration.getStatus() != PreRegistrationStatus.VALIDATED) {
            throw new IllegalStateException(
                    "Le dossier doit d'abord être pris en charge par le conseiller avant la conversion."
            );
        }

        preRegistration.setReferredCounselor(counselor);
        preRegistration.setCounselorAppointmentAt(LocalDateTime.now());

        Agency handlingAgency = extractAgencyFromAgent(counselor);
        if (handlingAgency != null) {
            preRegistration.setAgency(handlingAgency);
        }

        preRegistrationRepository.save(preRegistration);

        jobSeekerService.create(new JobSeekerRequest(
                preRegistration.getFirstName(),
                preRegistration.getLastName(),
                null,
                preRegistration.getDateOfBirth(),
                preRegistration.getPhone(),
                preRegistration.getEmail(),
                preRegistration.getCity(),
                null,
                preRegistration.getEducationLevel(),
                preRegistration.getPrimarySkill(),
                preRegistration.getProjectSummary(),
                "Plan d'action initial à préciser par le conseiller lors de la stabilisation du projet professionnel.",
                preRegistration.getRegistrationChannel() != null
                        && preRegistration.getRegistrationChannel().name().equals("HOME"),
                preRegistration.getAgency() != null ? preRegistration.getAgency().getId() : null,
                counselor.getId(),
                preRegistration.getId()
        ));

        PreRegistration refreshed = preRegistrationRepository.findById(preRegistrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Pré-inscription introuvable après conversion"));

        return toResponse(refreshed);
    }

    private PreRegistration buildPreRegistration(PreRegistrationRequest request) {
        PreRegistration preRegistration = new PreRegistration();

        preRegistration.setFirstName(request.firstName());
        preRegistration.setLastName(request.lastName());
        preRegistration.setPhone(request.phone());
        preRegistration.setEmail(request.email());
        preRegistration.setCity(request.city());
        preRegistration.setDateOfBirth(request.dateOfBirth());
        preRegistration.setEducationLevel(request.educationLevel());
        preRegistration.setPrimarySkill(request.primarySkill());
        preRegistration.setAutonomousOnPortal(request.autonomousOnPortal());
        preRegistration.setHasRequiredDocuments(request.hasRequiredDocuments());
        preRegistration.setProjectSummary(request.projectSummary());
        preRegistration.setWelcomeNotes(request.welcomeNotes());
        preRegistration.setSubmittedAt(LocalDateTime.now());
        preRegistration.setDocumentsVerifiedByScan(false);

        if (Boolean.TRUE.equals(request.hasRequiredDocuments())) {
            preRegistration.setStatus(PreRegistrationStatus.DOCUMENTS_PENDING);
        } else {
            preRegistration.setStatus(PreRegistrationStatus.SUBMITTED);
        }

        if (request.registrationChannel() != null) {
            preRegistration.setRegistrationChannel(request.registrationChannel());
        }

        if (request.agencyId() != null) {
            Agency agency = agencyRepository.findById(request.agencyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Agence introuvable"));
            preRegistration.setAgency(agency);
        }

        if (request.referredCounselorId() != null) {
            AgentProfile counselor = agentProfileRepository.findById(request.referredCounselorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Conseiller introuvable"));
            preRegistration.setReferredCounselor(counselor);

            if (preRegistration.getAgency() == null) {
                Agency handlingAgency = extractAgencyFromAgent(counselor);
                if (handlingAgency != null) {
                    preRegistration.setAgency(handlingAgency);
                }
            }
        }

        return preRegistration;
    }

    private Agency extractAgencyFromAgent(AgentProfile agentProfile) {
        if (agentProfile == null || agentProfile.getUser() == null) {
            return null;
        }
        return agentProfile.getUser().getAgency();
    }

    private synchronized String generateNextRequestNumber() {
        Long maxValue = preRegistrationRepository.findMaxRequestNumberValue();
        long nextValue = (maxValue == null || maxValue < REQUEST_START)
                ? REQUEST_START
                : maxValue + 1;

        String candidate = REQUEST_PREFIX + nextValue;

        while (preRegistrationRepository.existsByRequestNumber(candidate)) {
            nextValue++;
            candidate = REQUEST_PREFIX + nextValue;
        }

        return candidate;
    }

    private boolean isDuplicateRequestNumberException(Throwable ex) {
        Throwable current = ex;
        while (current != null) {
            String message = current.getMessage();

            if (message != null
                    && message.contains("Duplicate entry")
                    && message.contains("request_number")) {
                return true;
            }

            if (message != null
                    && message.contains("Duplicate entry")
                    && message.contains("pre_registrations")) {
                return true;
            }

            current = current.getCause();
        }
        return false;
    }

    private PreRegistrationResponse toResponse(PreRegistration preRegistration) {
        String agencyName = null;
        if (preRegistration.getAgency() != null) {
            agencyName = preRegistration.getAgency().getName();
        }

        String counselorName = null;
        if (preRegistration.getReferredCounselor() != null) {
            User counselorUser = preRegistration.getReferredCounselor().getUser();
            if (counselorUser != null && counselorUser.getFullName() != null) {
                counselorName = counselorUser.getFullName().trim();
            }
        }

        String documentsVerifiedByName = null;
        if (preRegistration.getDocumentsVerifiedBy() != null
                && preRegistration.getDocumentsVerifiedBy().getFullName() != null) {
            documentsVerifiedByName = preRegistration.getDocumentsVerifiedBy().getFullName().trim();
        }

        PnpeCardResponse pnpeCard = pnpeCardService.findByPreRegistrationIdOrNull(preRegistration.getId());

        return new PreRegistrationResponse(
                preRegistration.getId(),
                preRegistration.getRequestNumber(),
                preRegistration.getFirstName(),
                preRegistration.getLastName(),
                preRegistration.getPhone(),
                preRegistration.getEmail(),
                preRegistration.getCity(),
                preRegistration.getRegistrationChannel(),
                preRegistration.getStatus(),
                agencyName,
                counselorName,
                preRegistration.getSubmittedAt(),
                preRegistration.getDocumentsVerifiedByScan(),
                preRegistration.getDocumentsVerifiedAt(),
                documentsVerifiedByName,
                pnpeCard
        );
    }
}
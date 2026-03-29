package com.pnpe.backend.service.impl;

import com.pnpe.backend.dto.TrainingEnrollmentRequest;
import com.pnpe.backend.dto.TrainingProgramRequest;
import com.pnpe.backend.dto.TrainingProgramResponse;
import com.pnpe.backend.exception.ResourceNotFoundException;
import com.pnpe.backend.model.Agency;
import com.pnpe.backend.model.JobSeeker;
import com.pnpe.backend.model.TrainingEnrollment;
import com.pnpe.backend.model.TrainingProgram;
import com.pnpe.backend.model.enums.JobSeekerStatus;
import com.pnpe.backend.repository.AgencyRepository;
import com.pnpe.backend.repository.JobSeekerRepository;
import com.pnpe.backend.repository.TrainingEnrollmentRepository;
import com.pnpe.backend.repository.TrainingProgramRepository;
import com.pnpe.backend.service.TrainingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TrainingServiceImpl implements TrainingService {

    private final TrainingProgramRepository trainingProgramRepository;
    private final TrainingEnrollmentRepository trainingEnrollmentRepository;
    private final JobSeekerRepository jobSeekerRepository;
    private final AgencyRepository agencyRepository;

    public TrainingServiceImpl(TrainingProgramRepository trainingProgramRepository,
                               TrainingEnrollmentRepository trainingEnrollmentRepository,
                               JobSeekerRepository jobSeekerRepository,
                               AgencyRepository agencyRepository) {
        this.trainingProgramRepository = trainingProgramRepository;
        this.trainingEnrollmentRepository = trainingEnrollmentRepository;
        this.jobSeekerRepository = jobSeekerRepository;
        this.agencyRepository = agencyRepository;
    }

    @Override
    public TrainingProgramResponse createProgram(TrainingProgramRequest request) {
        TrainingProgram program = new TrainingProgram();
        program.setTitle(request.title());
        program.setDescription(request.description());
        program.setTrainerName(request.trainerName());
        program.setLocation(request.location());
        program.setStartDate(request.startDate());
        program.setEndDate(request.endDate());
        program.setCapacity(request.capacity());
        program.setStatus(request.status());

        if (request.agencyId() != null) {
            Agency agency = agencyRepository.findById(request.agencyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Agence introuvable"));
            program.setAgency(agency);
        }

        TrainingProgram savedProgram = trainingProgramRepository.save(program);
        return toResponse(savedProgram);
    }

    @Override
    public TrainingProgramResponse enroll(TrainingEnrollmentRequest request) {
        TrainingProgram program = trainingProgramRepository.findById(request.trainingProgramId())
                .orElseThrow(() -> new ResourceNotFoundException("Formation introuvable"));

        JobSeeker seeker = jobSeekerRepository.findById(request.jobSeekerId())
                .orElseThrow(() -> new ResourceNotFoundException("Demandeur introuvable"));

        TrainingEnrollment enrollment = new TrainingEnrollment();
        enrollment.setTrainingProgram(program);
        enrollment.setJobSeeker(seeker);
        trainingEnrollmentRepository.save(enrollment);

        seeker.setStatus(JobSeekerStatus.TRAINING_ORIENTED);
        jobSeekerRepository.save(seeker);

        return toResponse(program);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingProgramResponse> findAllPrograms() {
        return trainingProgramRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private TrainingProgramResponse toResponse(TrainingProgram program) {
        return new TrainingProgramResponse(
                program.getId(),
                program.getTitle(),
                program.getDescription(),
                program.getTrainerName(),
                program.getLocation(),
                program.getStartDate(),
                program.getEndDate(),
                program.getCapacity(),
                program.getStatus(),
                buildAgencyName(program.getAgency()),
                trainingEnrollmentRepository.findByTrainingProgramId(program.getId()).size()
        );
    }

    private String buildAgencyName(Agency agency) {
        if (agency == null) {
            return null;
        }

        if (agency.getName() == null || agency.getName().trim().isBlank()) {
            return null;
        }

        return agency.getName().trim();
    }
}
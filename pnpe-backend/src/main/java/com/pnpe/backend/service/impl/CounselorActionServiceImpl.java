package com.pnpe.backend.service.impl;

import com.pnpe.backend.dto.CounselorActionRequest;
import com.pnpe.backend.dto.CounselorActionResponse;
import com.pnpe.backend.dto.CounselorPerformanceResponse;
import com.pnpe.backend.exception.ResourceNotFoundException;
import com.pnpe.backend.model.AgentProfile;
import com.pnpe.backend.model.CounselorAction;
import com.pnpe.backend.model.JobSeeker;
import com.pnpe.backend.repository.*;
import com.pnpe.backend.service.CounselorActionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CounselorActionServiceImpl implements CounselorActionService {

    private final CounselorActionRepository actionRepository;
    private final JobSeekerRepository jobSeekerRepository;
    private final AgentProfileRepository agentProfileRepository;
    private final InterviewRepository interviewRepository;
    private final EmploymentPlacementRepository placementRepository;

    public CounselorActionServiceImpl(CounselorActionRepository actionRepository,
                                      JobSeekerRepository jobSeekerRepository,
                                      AgentProfileRepository agentProfileRepository,
                                      InterviewRepository interviewRepository,
                                      EmploymentPlacementRepository placementRepository) {
        this.actionRepository = actionRepository;
        this.jobSeekerRepository = jobSeekerRepository;
        this.agentProfileRepository = agentProfileRepository;
        this.interviewRepository = interviewRepository;
        this.placementRepository = placementRepository;
    }

    @Override
    public CounselorActionResponse create(CounselorActionRequest request) {
        JobSeeker seeker = jobSeekerRepository.findById(request.jobSeekerId())
                .orElseThrow(() -> new ResourceNotFoundException("Demandeur introuvable"));
        AgentProfile counselor = agentProfileRepository.findById(request.counselorId())
                .orElseThrow(() -> new ResourceNotFoundException("Conseiller introuvable"));
        CounselorAction action = new CounselorAction();
        action.setJobSeeker(seeker);
        action.setCounselor(counselor);
        action.setActionType(request.actionType());
        action.setActionDate(request.actionDate() != null ? request.actionDate() : LocalDateTime.now());
        action.setSummary(request.summary());
        action.setDetails(request.details());
        action.setNextActionDate(request.nextActionDate());
        return toResponse(actionRepository.save(action));
    }

    @Override
    public List<CounselorActionResponse> findByJobSeeker(Long jobSeekerId) {
        return actionRepository.findByJobSeekerIdOrderByActionDateDesc(jobSeekerId).stream().map(this::toResponse).toList();
    }

    @Override
    public CounselorPerformanceResponse performance(Long counselorId) {
        AgentProfile counselor = agentProfileRepository.findById(counselorId)
                .orElseThrow(() -> new ResourceNotFoundException("Conseiller introuvable"));
        return new CounselorPerformanceResponse(
                counselor.getId(),
                counselor.getUser().getFullName().trim(),
                counselor.getUser().getAgency() != null ? counselor.getUser().getAgency().getName() : null,
                jobSeekerRepository.countByAssignedAgentId(counselorId),
                interviewRepository.findByAgentProfileIdOrderByInterviewDateDesc(counselorId).size(),
                placementRepository.findByCounselorId(counselorId).size(),
                actionRepository.findByCounselorIdOrderByActionDateDesc(counselorId).size()
        );
    }

    private CounselorActionResponse toResponse(CounselorAction action) {
        return new CounselorActionResponse(
                action.getId(),
                action.getJobSeeker().getId(),
                action.getJobSeeker().getFirstName() + " " + action.getJobSeeker().getLastName(),
                action.getCounselor().getId(),
                action.getCounselor().getUser().getFullName().trim(),
                action.getActionType(),
                action.getActionDate(),
                action.getSummary(),
                action.getDetails(),
                action.getNextActionDate()
        );
    }
}

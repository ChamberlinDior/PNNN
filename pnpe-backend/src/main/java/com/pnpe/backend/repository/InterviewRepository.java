package com.pnpe.backend.repository;

import com.pnpe.backend.model.Interview;
import com.pnpe.backend.model.enums.InterviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
    long countByStatus(InterviewStatus status);
    List<Interview> findByCompanyIdOrderByInterviewDateDesc(Long companyId);
    List<Interview> findByJobSeekerIdOrderByInterviewDateDesc(Long jobSeekerId);
    List<Interview> findByAgentProfileIdOrderByInterviewDateDesc(Long agentProfileId);
}

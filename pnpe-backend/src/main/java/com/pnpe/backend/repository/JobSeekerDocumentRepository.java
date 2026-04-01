package com.pnpe.backend.repository;

import com.pnpe.backend.model.JobSeekerDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobSeekerDocumentRepository extends JpaRepository<JobSeekerDocument, Long> {
    List<JobSeekerDocument> findByJobSeekerIdOrderByCreatedAtDesc(Long jobSeekerId);
    List<JobSeekerDocument> findByPreRegistrationIdOrderByCreatedAtDesc(Long preRegistrationId);
}
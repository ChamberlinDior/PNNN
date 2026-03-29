package com.pnpe.backend.repository;

import com.pnpe.backend.model.TrainingEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingEnrollmentRepository extends JpaRepository<TrainingEnrollment, Long> {
    List<TrainingEnrollment> findByTrainingProgramId(Long trainingProgramId);
    List<TrainingEnrollment> findByJobSeekerId(Long jobSeekerId);
}

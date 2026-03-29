package com.pnpe.backend.model;

import com.pnpe.backend.model.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "training_enrollments")
public class TrainingEnrollment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_program_id", nullable = false)
    private TrainingProgram trainingProgram;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_seeker_id", nullable = false)
    private JobSeeker jobSeeker;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status = EnrollmentStatus.ASSIGNED;

    private String finalAssessment;
}

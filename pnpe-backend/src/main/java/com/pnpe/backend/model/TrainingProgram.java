package com.pnpe.backend.model;

import com.pnpe.backend.model.enums.TrainingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "training_programs")
public class TrainingProgram extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    private String trainerName;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    private TrainingStatus status = TrainingStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id")
    private Agency agency;
}

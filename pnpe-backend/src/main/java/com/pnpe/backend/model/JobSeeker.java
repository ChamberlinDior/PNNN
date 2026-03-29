package com.pnpe.backend.model;

import com.pnpe.backend.model.enums.Gender;
import com.pnpe.backend.model.enums.JobSeekerStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "job_seekers")
public class JobSeeker extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String dossierNumber;

    @Column(unique = true)
    private String openNumber;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate dateOfBirth;
    private String phone;
    private String email;
    private String city;
    private String address;
    private String educationLevel;
    private String primarySkill;
    private String projectSummary;
    private String actionPlanSummary;
    private String registrationSource;
    private String mainCvUrl;
    private LocalDateTime lastCvUpdateAt;
    private LocalDate lastContactDate;
    private LocalDateTime registrationValidatedAt;
    private Boolean selfRegistered = false;
    private Integer noShowCount = 0;
    private Boolean employedOutsidePnpe = false;

    @Enumerated(EnumType.STRING)
    private JobSeekerStatus status = JobSeekerStatus.PRE_REGISTERED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id")
    private Agency agency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_agent_id")
    private AgentProfile assignedAgent;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pre_registration_id")
    private PreRegistration preRegistration;
}

package com.pnpe.backend.model;

import com.pnpe.backend.model.enums.ContractType;
import com.pnpe.backend.model.enums.InterviewStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "interviews")
public class Interview extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_seeker_id", nullable = false)
    private JobSeeker jobSeeker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_profile_id", nullable = false)
    private AgentProfile agentProfile;

    @Column(nullable = false)
    private String jobTitle;

    @Column(nullable = false)
    private LocalDateTime interviewDate;

    private String location;
    private String mode;
    private String feedback;
    private String noShowReason;
    private String companyFeedback;
    private String applicantFeedback;
    private String followUpDecision;
    private LocalDateTime followUpAt;
    private LocalDateTime hiredAt;

    @Enumerated(EnumType.STRING)
    private InterviewStatus status = InterviewStatus.SCHEDULED;

    @Enumerated(EnumType.STRING)
    private ContractType proposedContractType;
}

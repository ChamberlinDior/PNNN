package com.pnpe.backend.model;

import com.pnpe.backend.model.enums.CounselorActionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "counselor_actions")
public class CounselorAction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_seeker_id", nullable = false)
    private JobSeeker jobSeeker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counselor_id", nullable = false)
    private AgentProfile counselor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CounselorActionType actionType;

    @Column(nullable = false)
    private LocalDateTime actionDate;

    @Column(nullable = false, length = 300)
    private String summary;

    @Lob
    private String details;

    private LocalDateTime nextActionDate;
}

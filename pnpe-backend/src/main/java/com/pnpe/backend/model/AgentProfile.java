package com.pnpe.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "agent_profiles")
public class AgentProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, unique = true)
    private String agentCode;

    private Integer monthlyTargetInsertions = 3;
    private Integer monthlyTargetInterviews = 20;
    private Boolean counselor = true;
    private String specialty;
}

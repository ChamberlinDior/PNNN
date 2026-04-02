package com.pnpe.backend.model;

import com.pnpe.backend.model.enums.PreRegistrationStatus;
import com.pnpe.backend.model.enums.RegistrationChannel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "pre_registrations")
public class PreRegistration extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String requestNumber;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String phone;
    private String email;
    private String city;
    private LocalDate dateOfBirth;
    private String educationLevel;
    private String primarySkill;
    private Boolean autonomousOnPortal;
    private Boolean hasRequiredDocuments;
    private String projectSummary;
    private String welcomeNotes;
    private String missingDocuments;

    private LocalDateTime submittedAt;

    /**
     * Rendez-vous d'accueil choisi par le demandeur.
     * Exemple : 2026-04-08T08:30
     */
    private LocalDateTime appointmentAt;

    /**
     * Ancien champ conservé pour compatibilité avec l’existant.
     * Peut continuer à être utilisé côté intranet si besoin.
     */
    private LocalDateTime counselorAppointmentAt;

    private Boolean documentsVerifiedByScan = false;
    private LocalDateTime documentsVerifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documents_verified_by_user_id")
    private User documentsVerifiedBy;

    @Enumerated(EnumType.STRING)
    private RegistrationChannel registrationChannel = RegistrationChannel.AGENCY;

    @Enumerated(EnumType.STRING)
    private PreRegistrationStatus status = PreRegistrationStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id")
    private Agency agency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referred_counselor_id")
    private AgentProfile referredCounselor;
}
package com.pnpe.backend.model;

import com.pnpe.backend.model.enums.DocumentOwnerType;
import com.pnpe.backend.model.enums.DocumentSide;
import com.pnpe.backend.model.enums.DocumentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "job_seeker_documents")
public class JobSeekerDocument extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentOwnerType ownerType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType documentType;

    @Enumerated(EnumType.STRING)
    private DocumentSide documentSide = DocumentSide.NOT_APPLICABLE;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String storagePath;

    private String mimeType;
    private Long sizeInBytes;
    private String label;
    private String documentNumber;
    private String issuingCountry;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private Boolean mandatory = false;
    private Boolean verified = false;
    private LocalDateTime scannedAt;
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pre_registration_id")
    private PreRegistration preRegistration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_seeker_id")
    private JobSeeker jobSeeker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_user_id")
    private User uploadedBy;
}

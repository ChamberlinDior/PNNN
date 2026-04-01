package com.pnpe.backend.model;

import com.pnpe.backend.model.enums.PnpeCardStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "pnpe_cards")
public class PnpeCard extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String cardNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PnpeCardStatus status = PnpeCardStatus.ACTIVE;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String qrCodeBase64;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String qrPayload;

    @Column(nullable = false)
    private Boolean generatedAutomatically = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pre_registration_id")
    private PreRegistration preRegistration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_by_user_id")
    private User generatedBy;
}
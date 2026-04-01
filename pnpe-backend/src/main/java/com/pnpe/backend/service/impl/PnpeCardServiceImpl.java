package com.pnpe.backend.service.impl;

import com.pnpe.backend.dto.PnpeCardResponse;
import com.pnpe.backend.model.PnpeCard;
import com.pnpe.backend.model.PreRegistration;
import com.pnpe.backend.model.User;
import com.pnpe.backend.model.enums.PnpeCardStatus;
import com.pnpe.backend.model.enums.RoleName;
import com.pnpe.backend.repository.PnpeCardRepository;
import com.pnpe.backend.repository.PreRegistrationRepository;
import com.pnpe.backend.repository.UserRepository;
import com.pnpe.backend.service.PnpeCardService;
import com.pnpe.backend.service.QrCodeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PnpeCardServiceImpl implements PnpeCardService {

    private static final String CARD_PREFIX = "PNPE-";
    private static final long CARD_START = 1001L;

    private final PnpeCardRepository pnpeCardRepository;
    private final PreRegistrationRepository preRegistrationRepository;
    private final UserRepository userRepository;
    private final QrCodeService qrCodeService;

    public PnpeCardServiceImpl(PnpeCardRepository pnpeCardRepository,
                               PreRegistrationRepository preRegistrationRepository,
                               UserRepository userRepository,
                               QrCodeService qrCodeService) {
        this.pnpeCardRepository = pnpeCardRepository;
        this.preRegistrationRepository = preRegistrationRepository;
        this.userRepository = userRepository;
        this.qrCodeService = qrCodeService;
    }

    @Override
    public PnpeCardResponse createForPreRegistration(Long preRegistrationId, Long scannerUserId) {
        PreRegistration preRegistration = preRegistrationRepository.findById(preRegistrationId)
                .orElseThrow(() -> new EntityNotFoundException("Pré-inscription introuvable"));

        if (pnpeCardRepository.existsByPreRegistrationId(preRegistrationId)) {
            return pnpeCardRepository.findByPreRegistrationId(preRegistrationId)
                    .map(this::toResponse)
                    .orElseThrow(() -> new EntityNotFoundException("Carte PNPE introuvable"));
        }

        User scanner = userRepository.findDetailedById(scannerUserId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur scanner introuvable"));

        if (scanner.getRole() == null || scanner.getRole().getName() != RoleName.ROLE_POLE_SCAN) {
            throw new IllegalStateException("Seul un utilisateur du pôle scan peut créer la carte PNPE.");
        }

        PnpeCard card = new PnpeCard();
        card.setCardNumber(generateNextCardNumber());
        card.setStatus(PnpeCardStatus.ACTIVE);
        card.setGeneratedAutomatically(true);
        card.setPreRegistration(preRegistration);
        card.setGeneratedBy(scanner);

        String payload = buildQrPayload(preRegistration, card.getCardNumber());
        card.setQrPayload(payload);
        card.setQrCodeBase64(qrCodeService.generateBase64Png(payload));

        return toResponse(pnpeCardRepository.save(card));
    }

    @Override
    @Transactional(readOnly = true)
    public PnpeCardResponse findByPreRegistrationId(Long preRegistrationId) {
        return pnpeCardRepository.findByPreRegistrationId(preRegistrationId)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Carte PNPE introuvable"));
    }

    @Override
    @Transactional(readOnly = true)
    public PnpeCardResponse findByPreRegistrationIdOrNull(Long preRegistrationId) {
        return pnpeCardRepository.findByPreRegistrationId(preRegistrationId)
                .map(this::toResponse)
                .orElse(null);
    }

    private synchronized String generateNextCardNumber() {
        Long maxValue = pnpeCardRepository.findMaxCardNumberValue();
        long nextValue = (maxValue == null || maxValue < CARD_START) ? CARD_START : maxValue + 1;

        String candidate = CARD_PREFIX + nextValue;
        while (pnpeCardRepository.existsByCardNumber(candidate)) {
            nextValue++;
            candidate = CARD_PREFIX + nextValue;
        }
        return candidate;
    }

    private String buildQrPayload(PreRegistration preRegistration, String cardNumber) {
        return """
                {
                  "cardNumber":"%s",
                  "preRegistrationId":%d,
                  "requestNumber":"%s",
                  "firstName":"%s",
                  "lastName":"%s",
                  "phone":"%s",
                  "email":"%s",
                  "city":"%s"
                }
                """.formatted(
                safe(cardNumber),
                preRegistration.getId(),
                safe(preRegistration.getRequestNumber()),
                safe(preRegistration.getFirstName()),
                safe(preRegistration.getLastName()),
                safe(preRegistration.getPhone()),
                safe(preRegistration.getEmail()),
                safe(preRegistration.getCity())
        );
    }

    private String safe(String value) {
        return value == null ? "" : value.replace("\"", "\\\"");
    }

    private PnpeCardResponse toResponse(PnpeCard card) {
        return new PnpeCardResponse(
                card.getId(),
                card.getCardNumber(),
                card.getStatus(),
                card.getQrCodeBase64(),
                card.getQrPayload(),
                card.getGeneratedAutomatically(),
                card.getCreatedAt()
        );
    }
}
package com.pnpe.backend.repository;

import com.pnpe.backend.model.PnpeCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PnpeCardRepository extends JpaRepository<PnpeCard, Long> {

    Optional<PnpeCard> findByCardNumber(String cardNumber);

    Optional<PnpeCard> findByPreRegistrationId(Long preRegistrationId);

    boolean existsByPreRegistrationId(Long preRegistrationId);

    boolean existsByCardNumber(String cardNumber);

    @Query(
            value = """
                    SELECT COALESCE(MAX(CAST(SUBSTRING(card_number, 6) AS UNSIGNED)), 1000)
                    FROM pnpe_cards
                    WHERE card_number IS NOT NULL
                      AND card_number LIKE 'PNPE-%'
                    """,
            nativeQuery = true
    )
    Long findMaxCardNumberValue();
}
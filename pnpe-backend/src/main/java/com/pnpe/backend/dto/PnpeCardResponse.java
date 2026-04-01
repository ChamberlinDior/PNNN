package com.pnpe.backend.dto;

import com.pnpe.backend.model.enums.PnpeCardStatus;

import java.time.LocalDateTime;

public record PnpeCardResponse(
        Long id,
        String cardNumber,
        PnpeCardStatus status,
        String qrCodeBase64,
        String qrPayload,
        Boolean generatedAutomatically,
        LocalDateTime createdAt
) {}
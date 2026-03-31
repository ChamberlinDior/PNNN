package com.pnpe.backend.dto.admin;

import com.pnpe.backend.model.enums.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UserStatusUpdateRequest(
        @NotNull(message = "Le statut de l'utilisateur est obligatoire")
        UserStatus status
) {
}
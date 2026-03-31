package com.pnpe.backend.dto.admin;

import jakarta.validation.constraints.NotNull;

public record AgencyStatusUpdateRequest(
        @NotNull(message = "Le statut actif/inactif est obligatoire")
        Boolean active
) {
}
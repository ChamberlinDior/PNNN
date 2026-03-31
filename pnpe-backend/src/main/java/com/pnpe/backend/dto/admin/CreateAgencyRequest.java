package com.pnpe.backend.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAgencyRequest(

        @NotBlank(message = "Le code de l'agence est obligatoire")
        @Size(max = 50, message = "Le code ne doit pas dépasser 50 caractères")
        String code,

        @NotBlank(message = "Le nom de l'agence est obligatoire")
        @Size(max = 150, message = "Le nom ne doit pas dépasser 150 caractères")
        String name,

        @Size(max = 100, message = "La ville ne doit pas dépasser 100 caractères")
        String city,

        @Size(max = 100, message = "La province ne doit pas dépasser 100 caractères")
        String province,

        @Size(max = 255, message = "L'adresse ne doit pas dépasser 255 caractères")
        String address,

        Boolean headquarters,

        Boolean active
) {
}
package com.pnpe.backend.dto.admin;

import com.pnpe.backend.model.enums.RoleName;
import com.pnpe.backend.model.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateAgencyUserRequest(

        @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères")
        String firstName,

        @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
        String lastName,

        @Email(message = "Email invalide")
        @Size(max = 150, message = "L'email ne doit pas dépasser 150 caractères")
        String email,

        @Size(max = 30, message = "Le téléphone ne doit pas dépasser 30 caractères")
        String phone,

        @Size(max = 255, message = "L'URL de photo ne doit pas dépasser 255 caractères")
        String profilePhotoUrl,

        @Size(max = 120, message = "Le poste ne doit pas dépasser 120 caractères")
        String jobTitle,

        @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
        String password,

        RoleName roleName,

        Long departmentId,

        UserStatus status,

        Boolean createAgentProfile,

        @Size(max = 100, message = "Le code agent ne doit pas dépasser 100 caractères")
        String agentCode,

        Integer monthlyTargetInsertions,

        Integer monthlyTargetInterviews,

        Boolean counselor,

        @Size(max = 150, message = "La spécialité ne doit pas dépasser 150 caractères")
        String specialty
) {
}
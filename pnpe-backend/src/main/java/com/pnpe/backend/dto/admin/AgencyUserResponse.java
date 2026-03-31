package com.pnpe.backend.dto.admin;

public record AgencyUserResponse(
        Long id,
        String firstName,
        String lastName,
        String fullName,
        String email,
        String phone,
        String profilePhotoUrl,
        String jobTitle,
        String status,
        Long roleId,
        String roleName,
        String roleLabel,
        Long agencyId,
        String agencyCode,
        String agencyName,
        Long departmentId,
        String departmentName,
        Long agentProfileId,
        String agentCode,
        Boolean counselor,
        Integer monthlyTargetInsertions,
        Integer monthlyTargetInterviews,
        String specialty
) {
}
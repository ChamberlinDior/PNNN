package com.pnpe.backend.dto.admin;

public record AgencyResponse(
        Long id,
        String code,
        String name,
        String city,
        String province,
        String address,
        Boolean headquarters,
        Boolean active,
        Long usersCount,
        Long departmentsCount
) {
}
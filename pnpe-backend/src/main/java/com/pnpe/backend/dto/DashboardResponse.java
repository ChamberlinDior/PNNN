package com.pnpe.backend.dto;

import java.util.List;

public record DashboardResponse(
        long totalUsers,
        long totalCounselors,
        long totalJobSeekers,
        long totalPreRegistrations,
        long totalCompanies,
        long totalInterviews,
        long scheduledInterviews,
        long completedInterviews,
        long totalPlacements,
        List<AgencyDashboardItem> agencies
) {
    public record AgencyDashboardItem(
            Long agencyId,
            String agencyCode,
            String agencyName,
            long counselors,
            long jobSeekers,
            long preRegistrations
    ) {}
}

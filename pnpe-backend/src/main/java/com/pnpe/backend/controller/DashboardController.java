package com.pnpe.backend.controller;

import com.pnpe.backend.dto.DashboardResponse;
import com.pnpe.backend.service.DashboardService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public DashboardResponse getSummary() {
        return dashboardService.getSummary();
    }
}

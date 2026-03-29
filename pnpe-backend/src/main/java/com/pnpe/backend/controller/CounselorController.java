package com.pnpe.backend.controller;

import com.pnpe.backend.dto.CounselorActionRequest;
import com.pnpe.backend.dto.CounselorActionResponse;
import com.pnpe.backend.dto.CounselorPerformanceResponse;
import com.pnpe.backend.service.CounselorActionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/counselors")
@CrossOrigin(origins = "*")
public class CounselorController {

    private final CounselorActionService counselorActionService;

    public CounselorController(CounselorActionService counselorActionService) {
        this.counselorActionService = counselorActionService;
    }

    @PostMapping("/actions")
    public CounselorActionResponse createAction(@Valid @RequestBody CounselorActionRequest request) {
        return counselorActionService.create(request);
    }

    @GetMapping("/actions/job-seeker/{jobSeekerId}")
    public List<CounselorActionResponse> history(@PathVariable Long jobSeekerId) {
        return counselorActionService.findByJobSeeker(jobSeekerId);
    }

    @GetMapping("/{counselorId}/performance")
    public CounselorPerformanceResponse performance(@PathVariable Long counselorId) {
        return counselorActionService.performance(counselorId);
    }
}

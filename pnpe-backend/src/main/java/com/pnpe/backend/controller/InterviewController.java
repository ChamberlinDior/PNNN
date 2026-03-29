package com.pnpe.backend.controller;

import com.pnpe.backend.dto.InterviewRequest;
import com.pnpe.backend.dto.InterviewResponse;
import com.pnpe.backend.dto.InterviewUpdateRequest;
import com.pnpe.backend.service.InterviewService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
@CrossOrigin(origins = "*")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @GetMapping
    public List<InterviewResponse> findAll() {
        return interviewService.findAll();
    }

    @PostMapping
    public InterviewResponse create(@Valid @RequestBody InterviewRequest request) {
        return interviewService.create(request);
    }

    @PatchMapping("/{id}/status")
    public InterviewResponse updateStatus(@PathVariable Long id,
                                          @Valid @RequestBody InterviewUpdateRequest request) {
        return interviewService.updateStatus(id, request);
    }
}

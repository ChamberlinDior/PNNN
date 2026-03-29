package com.pnpe.backend.controller;

import com.pnpe.backend.dto.JobSeekerRequest;
import com.pnpe.backend.dto.JobSeekerResponse;
import com.pnpe.backend.service.JobSeekerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-seekers")
@CrossOrigin(origins = "*")
public class JobSeekerController {

    private final JobSeekerService jobSeekerService;

    public JobSeekerController(JobSeekerService jobSeekerService) {
        this.jobSeekerService = jobSeekerService;
    }

    @GetMapping
    public List<JobSeekerResponse> findAll() {
        return jobSeekerService.findAll();
    }

    @GetMapping("/{id}")
    public JobSeekerResponse findById(@PathVariable Long id) {
        return jobSeekerService.findById(id);
    }

    @GetMapping("/search")
    public List<JobSeekerResponse> search(@RequestParam String keyword) {
        return jobSeekerService.search(keyword);
    }

    @PostMapping
    public JobSeekerResponse create(@Valid @RequestBody JobSeekerRequest request) {
        return jobSeekerService.create(request);
    }
}

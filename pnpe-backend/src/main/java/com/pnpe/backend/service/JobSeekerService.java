package com.pnpe.backend.service;

import com.pnpe.backend.dto.JobSeekerRequest;
import com.pnpe.backend.dto.JobSeekerResponse;

import java.util.List;

public interface JobSeekerService {
    JobSeekerResponse create(JobSeekerRequest request);
    List<JobSeekerResponse> findAll();
    List<JobSeekerResponse> search(String keyword);
    JobSeekerResponse findById(Long id);
}

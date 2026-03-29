package com.pnpe.backend.service;

import com.pnpe.backend.dto.InterviewRequest;
import com.pnpe.backend.dto.InterviewResponse;
import com.pnpe.backend.dto.InterviewUpdateRequest;

import java.util.List;

public interface InterviewService {
    InterviewResponse create(InterviewRequest request);
    List<InterviewResponse> findAll();
    InterviewResponse updateStatus(Long interviewId, InterviewUpdateRequest request);
}

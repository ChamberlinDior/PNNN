package com.pnpe.backend.service;

import com.pnpe.backend.dto.CounselorActionRequest;
import com.pnpe.backend.dto.CounselorActionResponse;
import com.pnpe.backend.dto.CounselorPerformanceResponse;

import java.util.List;

public interface CounselorActionService {
    CounselorActionResponse create(CounselorActionRequest request);
    List<CounselorActionResponse> findByJobSeeker(Long jobSeekerId);
    CounselorPerformanceResponse performance(Long counselorId);
}

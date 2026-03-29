package com.pnpe.backend.service;

import com.pnpe.backend.dto.TrainingEnrollmentRequest;
import com.pnpe.backend.dto.TrainingProgramRequest;
import com.pnpe.backend.dto.TrainingProgramResponse;

import java.util.List;

public interface TrainingService {
    TrainingProgramResponse createProgram(TrainingProgramRequest request);
    TrainingProgramResponse enroll(TrainingEnrollmentRequest request);
    List<TrainingProgramResponse> findAllPrograms();
}

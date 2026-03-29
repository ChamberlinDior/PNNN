package com.pnpe.backend.controller;

import com.pnpe.backend.dto.TrainingEnrollmentRequest;
import com.pnpe.backend.dto.TrainingProgramRequest;
import com.pnpe.backend.dto.TrainingProgramResponse;
import com.pnpe.backend.service.TrainingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainings")
@CrossOrigin(origins = "*")
public class TrainingController {

    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @GetMapping
    public List<TrainingProgramResponse> findAll() {
        return trainingService.findAllPrograms();
    }

    @PostMapping
    public TrainingProgramResponse create(@RequestBody TrainingProgramRequest request) {
        return trainingService.createProgram(request);
    }

    @PostMapping("/enrollments")
    public TrainingProgramResponse enroll(@RequestBody TrainingEnrollmentRequest request) {
        return trainingService.enroll(request);
    }
}

package com.pnpe.backend.service;

import com.pnpe.backend.dto.PlacementRequest;
import com.pnpe.backend.dto.PlacementResponse;

import java.util.List;

public interface PlacementService {
    PlacementResponse create(PlacementRequest request);
    List<PlacementResponse> findAll();
}

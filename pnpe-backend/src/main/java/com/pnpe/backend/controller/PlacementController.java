package com.pnpe.backend.controller;

import com.pnpe.backend.dto.PlacementRequest;
import com.pnpe.backend.dto.PlacementResponse;
import com.pnpe.backend.service.PlacementService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/placements")
@CrossOrigin(origins = "*")
public class PlacementController {

    private final PlacementService placementService;

    public PlacementController(PlacementService placementService) {
        this.placementService = placementService;
    }

    @GetMapping
    public List<PlacementResponse> findAll() {
        return placementService.findAll();
    }

    @PostMapping
    public PlacementResponse create(@RequestBody PlacementRequest request) {
        return placementService.create(request);
    }
}

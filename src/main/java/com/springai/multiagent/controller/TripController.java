package com.springai.multiagent.controller;

import com.springai.multiagent.dto.TripPlanningResponse;
import com.springai.multiagent.dto.TripRequest;
import com.springai.multiagent.services.TripPlanningService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripPlanningService orchestrator;

    public TripController(TripPlanningService orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping("/plan")
    public ResponseEntity<TripPlanningResponse> plan(@RequestBody TripRequest request) {
        TripPlanningResponse response = orchestrator.plan(request);

        return ResponseEntity.ok(response);
    }
}
package com.example.vocabulary_backend.controller;

import com.example.vocabulary_backend.model.TrainingSessionRequest;
import com.example.vocabulary_backend.model.TrainingSessionResponse;
import com.example.vocabulary_backend.service.TrainingSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TrainingController {

    @Autowired
    private TrainingSessionService service;

    @PostMapping("/training-sessions")
    public TrainingSessionResponse saveSession(@RequestBody TrainingSessionRequest request) {
        return service.saveSession(request);
    }

    @GetMapping("/training-sessions/recent")
    public List<TrainingSessionResponse> getRecentSessions(@RequestParam String scenarioId) {
        return service.getRecentSessions(scenarioId);
    }
}
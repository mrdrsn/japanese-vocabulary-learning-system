package com.example.vocabulary_backend.service;

import com.example.vocabulary_backend.model.TrainingSessionRequest;
import com.example.vocabulary_backend.model.TrainingSessionResponse;
import com.example.vocabulary_backend.model.postgres.TrainingSession;
import com.example.vocabulary_backend.repository.postgres.TrainingSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrainingSessionService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Autowired
    private TrainingSessionRepository repository;

    public TrainingSessionResponse saveSession(TrainingSessionRequest request) {
        // Сначала получаем предыдущую сессию, потом сохраняем новую
        Optional<TrainingSession> previous = repository.findTopByOrderByFinishedAtDesc();

        TrainingSession session = new TrainingSession();
        session.setScenarioId(request.getScenarioId());
        session.setFinishedAt(OffsetDateTime.now());
        session.setCorrectCount(request.getCorrectCount());
        session.setTotalCount(request.getTotalCount());
        session = repository.save(session);

        TrainingSessionResponse response = toResponse(session);
        previous.ifPresent(p -> response.setPreviousCorrectCount(p.getCorrectCount()));
        return response;
    }

    public List<TrainingSessionResponse> getRecentSessions() {
        return repository.findTop10ByOrderByFinishedAtDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private TrainingSessionResponse toResponse(TrainingSession s) {
        TrainingSessionResponse r = new TrainingSessionResponse();
        r.setId(s.getId());
        r.setScenarioId(s.getScenarioId());
        r.setFinishedAt(s.getFinishedAt().format(FMT));
        r.setCorrectCount(s.getCorrectCount());
        r.setTotalCount(s.getTotalCount());
        return r;
    }
}
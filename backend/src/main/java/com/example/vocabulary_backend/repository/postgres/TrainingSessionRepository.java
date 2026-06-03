package com.example.vocabulary_backend.repository.postgres;

import com.example.vocabulary_backend.model.postgres.TrainingSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {
    List<TrainingSession> findTop10ByOrderByFinishedAtDesc();
    Optional<TrainingSession> findTopByOrderByFinishedAtDesc();
}
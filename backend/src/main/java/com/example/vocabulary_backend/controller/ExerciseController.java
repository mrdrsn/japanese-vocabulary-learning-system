package com.example.vocabulary_backend.controller;

import com.example.vocabulary_backend.model.ExerciseTypeAResponse;
import com.example.vocabulary_backend.model.ExerciseTypeBResponse;
import com.example.vocabulary_backend.service.ExerciseService;
import org.springframework.web.bind.annotation.*;
import com.example.vocabulary_backend.model.ExerciseTypeCResponse;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ExerciseController {

    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping("/scenarios/{id}/exercises/type-a")
    public List<ExerciseTypeAResponse> getTypeAExercises(
            @PathVariable String id,
            @RequestParam(defaultValue = "2") int count,
            @RequestParam(defaultValue = "R2") String roleId) {
        return exerciseService.generateTypeAExercises(id, count, roleId);
    }
    @GetMapping("/scenarios/{id}/exercises/type-b")
    public List<ExerciseTypeBResponse> getTypeBExercises(
            @PathVariable String id,
            @RequestParam(defaultValue = "2") int count,
            @RequestParam(defaultValue = "auto") String roleId) {
        return exerciseService.generateTypeBExercises(id, count, roleId);
    }
    @GetMapping("/scenarios/{id}/exercises/type-c")
    public List<ExerciseTypeCResponse> getTypeCExercises(
            @PathVariable String id,
            @RequestParam(defaultValue = "2") int count) {
        return exerciseService.generateTypeCExercises(id, count);
    }
}
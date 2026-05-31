package com.example.japanesevocabularylearningsystem.network.dto;

import java.util.List;

public class ScenarioDto {
    public String id;
    public String name;
    public String displayName;
    public List<ScenarioStepDto> steps;
    public List<RoleDto> roles;
}
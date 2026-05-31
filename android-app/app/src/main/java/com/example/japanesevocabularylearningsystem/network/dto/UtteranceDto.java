package com.example.japanesevocabularylearningsystem.network.dto;

import java.util.List;

public class UtteranceDto {
    public String id;
    public String romaji;
    public String ruTranslation;
    public List<RoleDto> roles;
    public String audioUrl;
}
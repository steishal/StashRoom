package org.example.stashroom.dto;
import jakarta.validation.constraints.NotBlank;

public record CategoryCreateDTO(
        @NotBlank String name
) {}

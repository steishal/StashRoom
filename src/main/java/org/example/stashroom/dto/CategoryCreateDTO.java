package org.example.stashroom.dto;

import jakarta.validation.constraints.NotBlank;
// Для создания/обновления
public record CategoryCreateDTO(
        @NotBlank String name
) {}
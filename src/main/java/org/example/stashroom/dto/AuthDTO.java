package org.example.stashroom.dto;

import jakarta.validation.constraints.NotBlank;
// Для авторизации (входа)
public record AuthDTO(
        @NotBlank String username,
        @NotBlank String password
) {}

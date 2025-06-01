package org.example.stashroom.dto;
import jakarta.validation.constraints.NotBlank;

public record AuthDTO(
        @NotBlank String email,
        @NotBlank String password
) {}

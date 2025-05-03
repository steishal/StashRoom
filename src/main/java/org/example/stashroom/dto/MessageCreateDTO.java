package org.example.stashroom.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MessageCreateDTO(
        @NotBlank String content,
        @NotNull Long receiverId
) {}

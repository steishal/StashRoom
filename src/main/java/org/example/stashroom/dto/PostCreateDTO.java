package org.example.stashroom.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
// Для создания/обновления
public record PostCreateDTO(
        @NotBlank String content,
        @NotNull Long categoryId,
        List<String> images
) {}

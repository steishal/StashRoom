package org.example.stashroom.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
// Для создания
public record CommentCreateDTO(
        @NotBlank String content,
        @NotNull Long postId
) {}

package org.example.stashroom.dto;

import jakarta.validation.constraints.NotNull;
// Для создания/удаления
public record FollowerCreateDTO(
        @NotNull Long followerId,
        @NotNull Long followingId
) {}


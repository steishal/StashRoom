package org.example.stashroom.dto;
import jakarta.validation.constraints.NotNull;

public record FollowerCreateDTO(
        @NotNull Long followerId,
        @NotNull Long followingId
) {}

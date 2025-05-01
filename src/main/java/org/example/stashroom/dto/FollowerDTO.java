package org.example.stashroom.dto;
// Для отображения связей
public record FollowerDTO(
        Long followerId,
        String followerUsername,
        Long followingId,
        String followingUsername
) {}

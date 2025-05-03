package org.example.stashroom.dto;

public record FollowerDTO(
        Long followerId,
        String followerUsername,
        Long followingId,
        String followingUsername
) {}

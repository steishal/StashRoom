package org.example.stashroom.dto;
// Для отображения лайков
public record PostLikeDTO(
        Long postId,
        Long userId
) {}

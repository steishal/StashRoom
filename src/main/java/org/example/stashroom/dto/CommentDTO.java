package org.example.stashroom.dto;

import java.time.LocalDateTime;
// Для чтения данных
public record CommentDTO(
        String id,
        String content,
        String authorUsername,
        Long postId,
        LocalDateTime createDate
) {}
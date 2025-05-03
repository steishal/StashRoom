package org.example.stashroom.dto;
import java.time.LocalDateTime;

public record CommentDTO(
        String id,
        String content,
        String authorUsername,
        Long postId,
        LocalDateTime createDate
) {}

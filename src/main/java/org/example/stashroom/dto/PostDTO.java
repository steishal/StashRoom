package org.example.stashroom.dto;
import java.time.LocalDateTime;
import java.util.List;

public record PostDTO(
        Long id,
        String content,
        Long authorId,
        String authorUsername,
        CategoryDTO category,
        LocalDateTime createDate,
        List<String> images
) {}

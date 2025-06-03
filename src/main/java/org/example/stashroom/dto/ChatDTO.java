package org.example.stashroom.dto;
import java.time.LocalDateTime;

public record ChatDTO(
        Long userId,
        String username,
        String lastMessage,
        LocalDateTime timestamp
) {}

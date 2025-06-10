package org.example.stashroom.dto;

import java.time.LocalDateTime;

public record ChatPreviewDTO(
        Long chatWithUserId,
        String chatWithUsername,
        String lastMessageContent,
        String lastMessageSender,
        LocalDateTime sentAt
) {}

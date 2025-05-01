package org.example.stashroom.dto;

import java.time.LocalDateTime;
// Для чтения сообщений
public record MessageDTO(
        Long id,
        String content,
        Long senderId,
        String senderUsername,
        Long receiverId,
        String receiverUsername,
        LocalDateTime sentAt
) {}

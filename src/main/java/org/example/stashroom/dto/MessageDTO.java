package org.example.stashroom.dto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private Long id;
    private String content;
    private Long senderId;       // должно быть заполнено
    private Long receiverId;     // должно быть заполнено
    private String senderUsername;
    private String receiverUsername;
    private LocalDateTime sentAt;
    private Long tempId;         // для оптимистичных обновлений
    private String type;         // NEW, UPDATED, DELETED
}
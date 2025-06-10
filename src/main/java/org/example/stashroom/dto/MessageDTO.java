package org.example.stashroom.dto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private Long id;
    private String content;
    private Long senderId;
    private Long receiverId;
    private String senderUsername;
    private String receiverUsername;
    private LocalDateTime sentAt;
    private Long tempId;
    private String type;
}
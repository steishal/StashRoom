package org.example.stashroom.controllers;
import lombok.RequiredArgsConstructor;
import org.example.stashroom.dto.DeleteMessageRequest;
import org.example.stashroom.dto.MessageDTO;
import org.example.stashroom.dto.MessageInfo;
import org.example.stashroom.services.MessageService;
import org.example.stashroom.services.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final UserService userService;

    @MessageMapping("/chat/send")
    public void handleSend(MessageDTO dto, Principal principal) {
        String senderUsername = principal.getName();
        Long tempId = dto.getTempId();
        MessageDTO message = messageService.sendMessage(senderUsername, dto);
        message.setType("NEW");
        message.setTempId(tempId);
        String receiverUsername = userService.getUsernameById(message.getReceiverId());
        messagingTemplate.convertAndSendToUser(
                receiverUsername,
                "/queue/messages",
                message
        );

        messagingTemplate.convertAndSendToUser(
                senderUsername,
                "/queue/messages",
                message
        );
    }

    @MessageMapping("/chat/update")
    public void updateMessage(MessageDTO dto, Principal principal) {
        String username = principal.getName();
        MessageDTO updatedMessage = messageService.updateMessage(dto.getId(), username, dto);
        updatedMessage.setType("UPDATED");
        String receiverUsername = userService.getUsernameById(updatedMessage.getReceiverId());
        String senderUsername = userService.getUsernameById(updatedMessage.getSenderId());

        messagingTemplate.convertAndSendToUser(
                receiverUsername,
                "/queue/messages",
                updatedMessage
        );
        messagingTemplate.convertAndSendToUser(
                senderUsername,
                "/queue/messages",
                updatedMessage
        );
    }

    @MessageMapping("/chat/delete")
    public void deleteMessage(@Payload DeleteMessageRequest request, Principal principal) {
        String username = principal.getName();
        MessageInfo info = messageService.deleteMessage(request.messageId(), username);
        String receiverUsername = userService.getUsernameById(info.receiverId());
        String senderUsername = userService.getUsernameById(info.senderId());
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "DELETED");
        payload.put("messageId", request.messageId());

        messagingTemplate.convertAndSendToUser(
                receiverUsername,
                "/queue/messages",
                payload
        );
        messagingTemplate.convertAndSendToUser(
                senderUsername,
                "/queue/messages",
                payload
        );
    }
}

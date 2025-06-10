package org.example.stashroom.controllers;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.stashroom.dto.MessageCreateDTO;
import org.example.stashroom.dto.MessageDTO;
import org.example.stashroom.services.MessageService;
import org.example.stashroom.services.SecurityService;
import org.example.stashroom.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
        // ✅ Отправляем сообщение получателю
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

        // Отправляем обновление обоим участникам
        messagingTemplate.convertAndSendToUser(
                updatedMessage.getReceiverId().toString(),
                "/queue/messages",
                updatedMessage
        );
        messagingTemplate.convertAndSendToUser(
                updatedMessage.getSenderId().toString(),
                "/queue/messages",
                updatedMessage
        );
    }

    @MessageMapping("/chat/delete")
    public void deleteMessage(@Payload DeleteMessageRequest request, Principal principal) {
        String username = principal.getName();
        MessageInfo info = messageService.deleteMessage(request.messageId(), username);

        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "DELETED");
        payload.put("messageId", request.messageId());
        payload.put("senderId", info.senderId());
        payload.put("receiverId", info.receiverId());

        // Отправляем уведомление об удалении обоим участникам
        messagingTemplate.convertAndSendToUser(
                info.receiverId().toString(),
                "/queue/messages",
                payload
        );
        messagingTemplate.convertAndSendToUser(
                info.senderId().toString(),
                "/queue/messages",
                payload
        );
    }

    public record DeleteMessageRequest(Long messageId) {}
    public record MessageInfo(Long senderId, Long receiverId) {}
}

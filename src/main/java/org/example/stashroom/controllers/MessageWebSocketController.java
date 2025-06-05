package org.example.stashroom.controllers;
import lombok.RequiredArgsConstructor;
import org.example.stashroom.dto.MessageCreateDTO;
import org.example.stashroom.dto.MessageDTO;
import org.example.stashroom.services.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/chat")
    public void handleMessage(MessageCreateDTO dto, Principal principal) {
        String senderUsername = principal.getName();

        MessageDTO sent = messageService.sendMessage(senderUsername, dto);

        messagingTemplate.convertAndSendToUser(
                sent.receiverUsername(),
                "/queue/messages",
                sent
        );
    }
}

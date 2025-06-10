package org.example.stashroom.controllers;
import lombok.RequiredArgsConstructor;
import org.example.stashroom.dto.MessageDTO;
import org.example.stashroom.services.MessageService;
import org.example.stashroom.services.SecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final SecurityService securityService;

    @GetMapping("/conversations/{userId}")
    public ResponseEntity<List<MessageDTO>> getConversation(@PathVariable Long userId) {
        Long currentUserId = securityService.getCurrentUserId();
        List<MessageDTO> messages = messageService.findConversation(currentUserId, userId);
        return ResponseEntity.ok(messages);
    }
}

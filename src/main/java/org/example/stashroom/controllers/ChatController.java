package org.example.stashroom.controllers;

import lombok.RequiredArgsConstructor;
import org.example.stashroom.dto.ChatDTO;
import org.example.stashroom.services.MessageService;
import org.example.stashroom.services.SecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final MessageService messageService;
    private final SecurityService securityService;

    @GetMapping
    public ResponseEntity<List<ChatDTO>> getUserChats() {
        Long currentUserId = securityService.getCurrentUserId();
        List<ChatDTO> chats = messageService.getUserChats(currentUserId);
        return ResponseEntity.ok(chats);
    }
}

package org.example.stashroom.controllers;

import lombok.RequiredArgsConstructor;
import org.example.stashroom.dto.ChatDTO;
import org.example.stashroom.dto.ChatPreviewDTO;
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

    @GetMapping("/user/{userId}")
    public ResponseEntity<ChatDTO> getOrCreateChat(@PathVariable Long userId) {
        Long currentUserId = securityService.getCurrentUserId();
        ChatDTO chat = messageService.findOrCreateChat(currentUserId, userId);
        return ResponseEntity.ok(chat);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ChatPreviewDTO>> getChats() {
        Long currentUserId = securityService.getCurrentUserId();
        List<ChatPreviewDTO> chats = messageService.getChatPreviews(currentUserId);
        return ResponseEntity.ok(chats);
    }

}

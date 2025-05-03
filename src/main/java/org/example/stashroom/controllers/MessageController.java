package org.example.stashroom.controllers;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.stashroom.dto.MessageCreateDTO;
import org.example.stashroom.dto.MessageDTO;
import org.example.stashroom.services.MessageService;
import org.example.stashroom.services.SecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
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

    @PostMapping
    public ResponseEntity<MessageDTO> sendMessage(@Valid @RequestBody MessageCreateDTO dto) {
        String username = securityService.getCurrentUserUsername();
        MessageDTO sentMessage = messageService.sendMessage(username, dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(sentMessage.id())
                .toUri();

        return ResponseEntity.created(location).body(sentMessage);
    }

    @PutMapping("/{messageId}")
    public ResponseEntity<MessageDTO> updateMessage(
            @PathVariable Long messageId,
            @Valid @RequestBody MessageCreateDTO dto) {
        String username = securityService.getCurrentUserUsername();
        MessageDTO updatedMessage = messageService.updateMessage(messageId, username, dto);
        return ResponseEntity.ok(updatedMessage);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId) {
        String username = securityService.getCurrentUserUsername();
        messageService.deleteMessage(messageId, username);
        return ResponseEntity.noContent().build();
    }
}

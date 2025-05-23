package org.example.stashroom.services;
import org.example.stashroom.entities.User;
import org.example.stashroom.exceptions.NotFoundException;
import org.example.stashroom.exceptions.UnauthorizedException;
import org.example.stashroom.repositories.MessageRepository;
import org.example.stashroom.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.example.stashroom.dto.MessageDTO;
import org.example.stashroom.dto.MessageCreateDTO;
import org.example.stashroom.entities.Message;
import org.example.stashroom.mappers.MessageMapper;

@Service
@Transactional(readOnly = true)
@Slf4j
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;

    @Autowired
    public MessageService(MessageRepository messageRepository,
                          UserRepository userRepository,
                          MessageMapper messageMapper) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.messageMapper = messageMapper;
    }

    public List<MessageDTO> findConversation(Long userA, Long userB) {
        log.debug("Fetching conversation between users {} and {}", userA, userB);
        return messageRepository.findConversation(userA, userB).stream()
                .map(messageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public MessageDTO sendMessage(String senderUsername, MessageCreateDTO dto) {
        log.info("Sending message from {} to {}", senderUsername, dto.receiverId());

        User sender = userRepository.findByUsernameIgnoreCase(senderUsername)
                .orElseThrow(() -> {
                    log.error("Sender not found: {}", senderUsername);
                    return new NotFoundException("User not found");
                });

        User receiver = userRepository.findById(dto.receiverId())
                .orElseThrow(() -> {
                    log.error("Receiver not found: {}", dto.receiverId());
                    return new NotFoundException("Receiver not found");
                });

        Message msg = new Message();
        msg.setContent(dto.content());
        msg.setSender(sender);
        msg.setReceiver(receiver);
        msg.setSentAt(LocalDateTime.now());

        Message saved = messageRepository.save(msg);
        log.info("Message sent with ID: {}", saved.getId());
        return messageMapper.toDto(saved);
    }

    @Transactional
    public MessageDTO updateMessage(Long messageId, String currentUsername, MessageCreateDTO dto) {
        log.info("Updating message ID: {}", messageId);

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> {
                    log.error("Message not found: {}", messageId);
                    return new NotFoundException("Message not found");
                });

        User currentUser = userRepository.findByUsernameIgnoreCase(currentUsername)
                .orElseThrow(() -> {
                    log.error("User not found: {}", currentUsername);
                    return new NotFoundException("User not found");
                });

        if (!message.getSender().equals(currentUser)) {
            log.warn("Unauthorized edit attempt by user: {}", currentUsername);
            throw new UnauthorizedException("You can only edit your own messages");
        }

        message.setContent(dto.content());
        Message updated = messageRepository.save(message);
        log.debug("Message updated: {}", messageId);
        return messageMapper.toDto(updated);
    }

    @Transactional
    public void deleteMessage(Long messageId, String currentUsername) {
        log.info("Deleting message ID: {}", messageId);

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> {
                    log.error("Message not found: {}", messageId);
                    return new NotFoundException("Message not found");
                });

        User currentUser = userRepository.findByUsernameIgnoreCase(currentUsername)
                .orElseThrow(() -> {
                    log.error("User not found: {}", currentUsername);
                    return new NotFoundException("User not found");
                });

        if (!message.getSender().equals(currentUser)) {
            log.warn("Unauthorized delete attempt by user: {}", currentUsername);
            throw new UnauthorizedException("You can only delete your own messages");
        }

        messageRepository.delete(message);
        log.debug("Message deleted: {}", messageId);
    }
}

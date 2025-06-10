package org.example.stashroom.services;
import jakarta.persistence.EntityNotFoundException;
import org.example.stashroom.controllers.MessageWebSocketController;
import org.example.stashroom.dto.*;
import org.example.stashroom.entities.User;
import org.example.stashroom.exceptions.MessageEditTimeExpiredException;
import org.example.stashroom.exceptions.NotFoundException;
import org.example.stashroom.exceptions.UnauthorizedException;
import org.example.stashroom.repositories.MessageRepository;
import org.example.stashroom.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public List<ChatDTO> getUserChats(Long currentUserId) {
        List<Object[]> results = messageRepository.findChatsForUser(currentUserId);
        return results.stream().map(row -> new ChatDTO(
                (Long) row[0],
                (String) row[1],
                (String) row[2],
                (LocalDateTime) row[3]
        )).toList();
    }

    @Transactional(readOnly = true)
    public List<MessageDTO> findConversation(Long userA, Long userB) {
        log.debug("Fetching conversation between users {} and {}", userA, userB);
        return messageRepository.findConversation(userA, userB).stream()
                .map(messageMapper::toDto)
                .toList();
    }

    @Transactional
    public MessageDTO sendMessage(String senderUsername, MessageDTO dto) {
        log.info("Sending message from {} to {}", senderUsername, dto.getReceiverId());

        User sender = userRepository.findByUsernameIgnoreCase(senderUsername)
                .orElseThrow(() -> new NotFoundException("Sender not found"));

        User receiver = userRepository.findById(dto.getReceiverId())
                .orElseThrow(() -> new NotFoundException("Receiver not found"));

        Message msg = new Message();
        msg.setContent(dto.getContent());
        msg.setSender(sender);
        msg.setReceiver(receiver);
        msg.setSentAt(LocalDateTime.now());

        Message saved = messageRepository.save(msg);
        log.info("Message sent with ID: {}", saved.getId());

        MessageDTO responseDto = messageMapper.toDto(saved);
        responseDto.setTempId(dto.getTempId());
        responseDto.setSenderId(sender.getId());
        responseDto.setReceiverId(receiver.getId());
        responseDto.setSenderUsername(sender.getUsername());
        responseDto.setReceiverUsername(receiver.getUsername());

        return responseDto;
    }


    @Transactional
    public MessageDTO updateMessage(Long messageId, String currentUsername, MessageDTO dto) {
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

        if (Duration.between(message.getSentAt(), LocalDateTime.now()).toHours() >= 24) {
            log.warn("Edit time expired for message ID: {}", messageId);
            throw new MessageEditTimeExpiredException();
        }

        message.setContent(dto.getContent());

        Message updated = messageRepository.save(message);
        log.debug("Message updated: {}", messageId);
        return messageMapper.toDto(updated);
    }



    @Transactional
    public MessageInfo deleteMessage(Long messageId, String currentUsername) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("Message not found"));

        User currentUser = userRepository.findByUsernameIgnoreCase(currentUsername)
                .orElseThrow(() -> {
                    log.error("User not found: {}", currentUsername);
                    return new NotFoundException("User not found");
                });

        if (!message.getSender().equals(currentUser)) {
            log.warn("Unauthorized delete attempt by user: {}", currentUsername);
            throw new UnauthorizedException("You can only delete your own messages");
        }

        Long senderId = message.getSender().getId();
        Long receiverId = message.getReceiver().getId();

        messageRepository.delete(message);

        return new MessageInfo(senderId, receiverId);
    }

    public ChatDTO findOrCreateChat(Long user1Id, Long user2Id) {
        User user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new NotFoundException("User1 not found"));
        User user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new NotFoundException("User2 not found"));

        Message latestMessage = messageRepository.findLatestMessageBetween(user1Id, user2Id);

        String lastMessageText = latestMessage != null ? latestMessage.getContent() : null;
        LocalDateTime lastMessageTime = latestMessage != null ? latestMessage.getSentAt() : null;

        return new ChatDTO(
                user2.getId(),
                user2.getUsername(),
                lastMessageText,
                lastMessageTime
        );
    }

    public List<ChatPreviewDTO> getChatPreviews(Long userId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Message> lastMessages = messageRepository.findLastMessagesForEachChat(currentUser);

        return lastMessages.stream()
                .map(message -> {
                    User chatWith = message.getSender().equals(currentUser)
                            ? message.getReceiver()
                            : message.getSender();

                    return new ChatPreviewDTO(
                            chatWith.getId(),
                            chatWith.getUsername(),
                            message.getContent(),
                            message.getSender().getUsername(),
                            message.getSentAt()
                    );
                })
                .toList();
    }
}

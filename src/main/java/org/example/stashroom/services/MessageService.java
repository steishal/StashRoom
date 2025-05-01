package org.example.stashroom.services;
import org.example.stashroom.entities.User;
import org.example.stashroom.repositories.MessageRepository;
import org.example.stashroom.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.example.stashroom.dto.MessageDTO;
import org.example.stashroom.dto.MessageCreateDTO;
import org.example.stashroom.entities.Message;
import org.example.stashroom.mappers.MessageMapper;

@Service
@Transactional(readOnly = true)
public class MessageService {
    @Autowired private MessageRepository messageRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private MessageMapper messageMapper;

    public List<MessageDTO> findConversation(Long userA, Long userB) {
        return messageRepository.findConversation(userA, userB).stream()
                .map(messageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public MessageDTO sendMessage(String senderUsername, MessageCreateDTO dto) {
        User sender = userRepository.findByUsernameIgnoreCase(senderUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User receiver = userRepository.findById(dto.receiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Message msg = new Message();
        msg.setContent(dto.content());
        msg.setSender(sender);
        msg.setReceiver(receiver);
        msg.setSentAt(LocalDateTime.now());

        Message saved = messageRepository.save(msg);
        return messageMapper.toDto(saved);
    }
}

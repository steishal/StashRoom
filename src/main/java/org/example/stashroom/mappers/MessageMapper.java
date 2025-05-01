package org.example.stashroom.mappers;
import org.example.stashroom.dto.MessageCreateDTO;
import org.example.stashroom.dto.MessageDTO;
import org.example.stashroom.entities.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    @Mapping(target = "senderUsername", source = "sender.username")
    @Mapping(target = "receiverUsername", source = "receiver.username")
    MessageDTO toDto(Message message);
    @Mapping(target = "content", source = "dto.content")
    @Mapping(target = "receiver", ignore = true)
    @Mapping(target = "sender", ignore = true)
    @Mapping(target = "sentAt", expression = "java(java.time.LocalDateTime.now())")
    Message fromCreateDto(MessageCreateDTO dto);
}

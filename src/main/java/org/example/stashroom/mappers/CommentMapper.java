package org.example.stashroom.mappers;
import org.example.stashroom.dto.CommentCreateDTO;
import org.example.stashroom.dto.CommentDTO;
import org.example.stashroom.entities.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "authorUsername", source = "author.username")
    CommentDTO toDto(Comment comment);
    @Mapping(target = "content", source = "dto.content")
    @Mapping(target = "post", expression = "java(new Post(dto.postId(), null, null, null, null, null))")
    @Mapping(target = "createDate", expression = "java(java.time.LocalDateTime.now())")
    Comment fromCreateDto(CommentCreateDTO dto);
}

package org.example.stashroom.mappers;
import org.example.stashroom.dto.CommentCreateDTO;
import org.example.stashroom.dto.CommentDTO;
import org.example.stashroom.entities.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PostMapper.class})
public interface CommentMapper {
    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "authorUsername", source = "author.username")
    CommentDTO toDto(Comment comment);
    @Mapping(target = "post", source = "postId")
    @Mapping(target = "content", source = "dto.content")
    @Mapping(target = "createDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    Comment fromCreateDto(CommentCreateDTO dto);
}

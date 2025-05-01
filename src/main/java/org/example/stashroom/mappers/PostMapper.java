package org.example.stashroom.mappers;
import org.example.stashroom.dto.PostCreateDTO;
import org.example.stashroom.dto.PostDTO;
import org.example.stashroom.entities.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { CategoryMapper.class })
public interface PostMapper {
    @Mapping(target = "authorUsername", source = "author.username")
    @Mapping(target = "category", source = "category")
    PostDTO toDto(Post post);
    @Mapping(target = "content", source = "dto.content")
    @Mapping(target = "category", expression = "java(new Category(dto.categoryId(), null))")
    Post fromCreateDto(PostCreateDTO dto);
}

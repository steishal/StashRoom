package org.example.stashroom.mappers;
import org.example.stashroom.dto.PostCreateDTO;
import org.example.stashroom.dto.PostDTO;
import org.example.stashroom.entities.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { CategoryMapper.class })
public interface PostMapper {
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorUsername", source = "author.username")
    @Mapping(target = "category", source = "category")
    PostDTO toDto(Post post);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "createDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "category", source = "categoryId")
    Post fromCreateDto(PostCreateDTO dto);
    default Post fromId(Long postId) {
        if (postId == null) return null;
        Post post = new Post();
        post.setId(postId);
        return post;
    }
}

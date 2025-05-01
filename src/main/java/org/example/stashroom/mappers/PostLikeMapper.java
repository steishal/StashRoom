package org.example.stashroom.mappers;
import org.example.stashroom.dto.PostLikeDTO;
import org.example.stashroom.entities.PostLike;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostLikeMapper {
    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "userId", source = "user.id")
    PostLikeDTO toDto(PostLike like);
}

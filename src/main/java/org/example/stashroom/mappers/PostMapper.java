package org.example.stashroom.mappers;
import org.example.stashroom.dto.PostCreateDTO;
import org.example.stashroom.dto.PostDTO;
import org.example.stashroom.entities.Post;
import org.example.stashroom.entities.PostLike;
import org.example.stashroom.repositories.CommentRepository;
import org.example.stashroom.repositories.PostLikeRepository;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring", uses = { CategoryMapper.class })
public interface PostMapper {

    @Mapping(target = "likeCount", ignore = true)
    @Mapping(target = "author", source = "author")
    @Mapping(target = "likedByCurrentUser", ignore = true)
    @Mapping(target = "commentsCount", ignore = true)
    PostDTO toDto(
            Post post,
            @Context Long currentUserId,
            @Context PostLikeRepository likeRepo,
            @Context CommentRepository commentRepo
    );

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

    @AfterMapping
    default void enrichPostDTO(
            Post post,
            @Context Long currentUserId,
            @MappingTarget PostDTO dto,
            @Context PostLikeRepository likeRepo,
            @Context CommentRepository commentRepo
    ) {
        List<PostLike> likes = likeRepo.findByPostId(post.getId());

        dto.setLikeCount(likes.size());
        dto.setLikedByCurrentUser(
                currentUserId != null &&
                        likes.stream().anyMatch(like -> like.getUser().getId().equals(currentUserId))
        );

        dto.setCommentsCount(commentRepo.countByPostId(post.getId()));
    }
}

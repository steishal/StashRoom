package org.example.stashroom.services;
import org.example.stashroom.dto.PostLikeDTO;
import org.example.stashroom.entities.Post;
import org.example.stashroom.entities.PostLike;
import org.example.stashroom.entities.User;
import org.example.stashroom.exceptions.DuplicateEntityException;
import org.example.stashroom.exceptions.NotFoundException;
import org.example.stashroom.mappers.PostLikeMapper;
import org.example.stashroom.repositories.PostLikeRepository;
import org.example.stashroom.repositories.PostRepository;
import org.example.stashroom.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@Slf4j
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostLikeMapper postLikeMapper;

    @Autowired
    public PostLikeService(PostLikeRepository postLikeRepository,
                           UserRepository userRepository,
                           PostRepository postRepository,
                           PostLikeMapper postLikeMapper) {
        this.postLikeRepository = postLikeRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.postLikeMapper = postLikeMapper;
    }

    public List<PostLikeDTO> findLikesByPost(Long postId) {
        log.debug("Fetching likes for post ID: {}", postId);
        return postLikeRepository.findByPostId(postId).stream()
                .map(postLikeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void like(Long postId, Long userId) {
        log.info("User {} liking post {}", userId, postId);

        if (postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            log.warn("Like already exists for user {} and post {}", userId, postId);
            throw new DuplicateEntityException("Like already exists");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found: {}", userId);
                    return new NotFoundException("User not found");
                });

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error("Post not found: {}", postId);
                    return new NotFoundException("Post not found");
                });

        PostLike like = new PostLike();
        like.setPost(post);
        like.setUser(user);
        postLikeRepository.save(like);
        log.info("Like added for post {} by user {}", postId, userId);
    }

    @Transactional
    public void unlike(Long postId, Long userId) {
        log.info("User {} unliking post {}", userId, postId);
        if (!postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            log.warn("Like not found for deletion");
            throw new NotFoundException("Like not found");
        }
        postLikeRepository.deleteByPostIdAndUserId(postId, userId);
        log.debug("Like removed successfully");
    }
}

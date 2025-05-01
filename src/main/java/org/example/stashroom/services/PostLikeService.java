package org.example.stashroom.services;
import org.example.stashroom.dto.PostLikeDTO;
import org.example.stashroom.entities.Post;
import org.example.stashroom.entities.PostLike;
import org.example.stashroom.entities.User;
import org.example.stashroom.mappers.PostLikeMapper;
import org.example.stashroom.repositories.PostLikeRepository;
import org.example.stashroom.repositories.PostRepository;
import org.example.stashroom.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PostLikeService {
    @Autowired private PostLikeRepository postLikeRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private PostLikeMapper postLikeMapper;

    public List<PostLikeDTO> findLikesByPost(Long postId) {
        return postLikeRepository.findByPostId(postId).stream()
                .map(postLikeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void like(Long postId, Long userId) {
        if (postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            return; // already liked
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        PostLike like = new PostLike();
        like.setPost(post);
        like.setUser(user);
        postLikeRepository.save(like);
    }

    @Transactional
    public void unlike(Long postId, Long userId) {
        postLikeRepository.deleteByPostIdAndUserId(postId, userId);
    }
}

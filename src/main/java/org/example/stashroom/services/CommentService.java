package org.example.stashroom.services;
import lombok.extern.slf4j.Slf4j;
import org.example.stashroom.dto.CommentCreateDTO;
import org.example.stashroom.dto.CommentDTO;
import org.example.stashroom.entities.Comment;
import org.example.stashroom.entities.Post;
import org.example.stashroom.entities.User;
import org.example.stashroom.exceptions.NotFoundException;
import org.example.stashroom.exceptions.UnauthorizedException;
import org.example.stashroom.mappers.CommentMapper;
import org.example.stashroom.repositories.CommentRepository;
import org.example.stashroom.repositories.PostRepository;
import org.example.stashroom.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Autowired
    public CommentService(CommentRepository commentRepository,
                          PostRepository postRepository,
                          UserRepository userRepository,
                          CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentMapper = commentMapper;
    }

    public List<CommentDTO> findByPost(Long postId) {
        log.debug("Fetching comments for post: {}", postId);
        return commentRepository.findByPostId(postId).stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDTO create(String authorUsername, CommentCreateDTO dto) {
        log.info("Creating comment by user: {}", authorUsername);
        User author = userRepository.findByEmailIgnoreCase(authorUsername)
                .orElseThrow(() -> {
                    log.error("User not found: {}", authorUsername);
                    return new NotFoundException("User not found");
                });

        Post post = postRepository.findById(dto.postId())
                .orElseThrow(() -> {
                    log.error("Post not found: {}", dto.postId());
                    return new NotFoundException("Post not found");
                });

        Comment comment = commentMapper.fromCreateDto(dto);
        comment.setAuthor(author);
        comment.setPost(post);
        Comment saved = commentRepository.save(comment);
        log.info("Comment created with id: {}", saved.getId());
        return commentMapper.toDto(saved);
    }

    @Transactional
    public void delete(String id) {
        log.info("Deleting comment: {}", id);
        commentRepository.deleteById(id);
        log.debug("Comment deleted: {}", id);
    }

    @Transactional
    public CommentDTO update(String commentId, String authorUsername, CommentCreateDTO dto) {
        log.info("Updating comment: {}", commentId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.error("Comment not found: {}", commentId);
                    return new NotFoundException("Comment not found");
                });

        User author = userRepository.findByUsernameIgnoreCase(authorUsername)
                .orElseThrow(() -> {
                    log.error("User not found: {}", authorUsername);
                    return new NotFoundException("User not found");
                });

        if (!comment.getAuthor().getId().equals(author.getId())) {
            log.warn("Unauthorized edit attempt by user: {}", authorUsername);
            throw new UnauthorizedException("Not authorized to edit comment");
        }

        comment.setContent(dto.content());
        Comment updated = commentRepository.save(comment);
        log.debug("Comment updated: {}", commentId);
        return commentMapper.toDto(updated);
    }
}

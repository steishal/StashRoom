package org.example.stashroom.services;
import org.example.stashroom.dto.CommentCreateDTO;
import org.example.stashroom.dto.CommentDTO;
import org.example.stashroom.entities.Comment;
import org.example.stashroom.entities.Post;
import org.example.stashroom.entities.User;
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
public class CommentService {
    @Autowired private CommentRepository commentRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CommentMapper commentMapper;

    public List<CommentDTO> findByPost(Long postId) {
        return commentRepository.findByPostId(postId).stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDTO create(String authorUsername, CommentCreateDTO dto) {
        User author = userRepository.findByUsernameIgnoreCase(authorUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(dto.postId())
                .orElseThrow(() -> new RuntimeException("Post not found"));
        Comment comment = commentMapper.fromCreateDto(dto);
        comment.setAuthor(author);
        comment.setPost(post);
        Comment saved = commentRepository.save(comment);
        return commentMapper.toDto(saved);
    }

    @Transactional
    public void delete(String id) {
        commentRepository.deleteById(id);
    }
}

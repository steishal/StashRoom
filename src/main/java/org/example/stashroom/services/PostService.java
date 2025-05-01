package org.example.stashroom.services;
import org.example.stashroom.dto.PostCreateDTO;
import org.example.stashroom.dto.PostDTO;
import org.example.stashroom.entities.Category;
import org.example.stashroom.entities.Post;
import org.example.stashroom.entities.User;
import org.example.stashroom.mappers.PostMapper;
import org.example.stashroom.repositories.CategoryRepository;
import org.example.stashroom.repositories.PostRepository;
import org.example.stashroom.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
@Service
@Transactional(readOnly = true)
public class PostService {
    @Autowired private PostRepository postRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private PostMapper postMapper;

    public List<PostDTO> findAll() {
        return postRepository.findAll().stream()
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    public PostDTO findById(Long id) {
        return postRepository.findById(id)
                .map(postMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    @Transactional
    public PostDTO create(String authorUsername, PostCreateDTO dto) {
        User author = userRepository.findByUsernameIgnoreCase(authorUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Post post = postMapper.fromCreateDto(dto);
        post.setAuthor(author);
        post.setCategory(category);
        Post saved = postRepository.save(post);
        return postMapper.toDto(saved);
    }

    @Transactional
    public PostDTO update(Long id, PostCreateDTO dto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        post.setContent(dto.content());
        post.setCategory(category);
        post.setImages(dto.images() == null ? List.of() : dto.images());
        return postMapper.toDto(postRepository.save(post));
    }

    @Transactional
    public void delete(Long id) {
        postRepository.deleteById(id);
    }
}

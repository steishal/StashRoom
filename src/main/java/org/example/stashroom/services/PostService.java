package org.example.stashroom.services;
import lombok.extern.slf4j.Slf4j;
import org.example.stashroom.exceptions.NotFoundException;
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
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PostMapper postMapper;

    @Autowired
    public PostService(PostRepository postRepository,
                       UserRepository userRepository,
                       CategoryRepository categoryRepository,
                       PostMapper postMapper) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.postMapper = postMapper;
    }

    public List<PostDTO> findAll() {
        log.debug("Fetching all posts");
        return postRepository.findAll().stream()
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    public PostDTO findById(Long id) {
        log.debug("Fetching post by ID: {}", id);
        return postRepository.findById(id)
                .map(postMapper::toDto)
                .orElseThrow(() -> {
                    log.error("Post not found with ID: {}", id);
                    return new NotFoundException("Post not found");
                });
    }

    @Transactional
    public PostDTO create(String authorUsername, PostCreateDTO dto) {
        log.info("Creating new post by user: {}", authorUsername);

        User author = userRepository.findByUsernameIgnoreCase(authorUsername)
                .orElseThrow(() -> {
                    log.error("Author not found: {}", authorUsername);
                    return new NotFoundException("User not found");
                });

        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> {
                    log.error("Category not found: {}", dto.categoryId());
                    return new NotFoundException("Category not found");
                });

        Post post = postMapper.fromCreateDto(dto);
        post.setAuthor(author);
        post.setCategory(category);

        Post saved = postRepository.save(post);
        log.info("Post created with ID: {}", saved.getId());
        return postMapper.toDto(saved);
    }

    @Transactional
    public PostDTO update(Long id, PostCreateDTO dto) {
        log.info("Updating post ID: {}", id);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Post not found for update: {}", id);
                    return new NotFoundException("Post not found");
                });

        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> {
                    log.error("Category not found: {}", dto.categoryId());
                    return new NotFoundException("Category not found");
                });

        post.setContent(dto.content());
        post.setCategory(category);
        post.setImages(dto.images() == null ? List.of() : dto.images());

        Post updated = postRepository.save(post);
        log.debug("Post updated successfully: {}", id);
        return postMapper.toDto(updated);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting post ID: {}", id);
        if (!postRepository.existsById(id)) {
            log.warn("Post not found for deletion: {}", id);
            throw new NotFoundException("Post not found");
        }
        postRepository.deleteById(id);
        log.debug("Post deleted: {}", id);
    }
}

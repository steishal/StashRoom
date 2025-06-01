package org.example.stashroom.services;
import lombok.extern.slf4j.Slf4j;
import org.example.stashroom.exceptions.NotFoundException;
import org.example.stashroom.dto.PostCreateDTO;
import org.example.stashroom.dto.PostDTO;
import org.example.stashroom.entities.Category;
import org.example.stashroom.entities.Post;
import org.example.stashroom.entities.User;
import org.example.stashroom.mappers.PostMapper;
import org.example.stashroom.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final PostMapper postMapper;
    private final SecurityService securityService;

    @Autowired
    public PostService(PostRepository postRepository,
                       UserRepository userRepository,
                       CategoryRepository categoryRepository,
                       PostLikeRepository postLikeRepository,
                       CommentRepository commentRepository,
                       PostMapper postMapper,
                       SecurityService securityService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.postLikeRepository = postLikeRepository;
        this.commentRepository = commentRepository;
        this.postMapper = postMapper;
        this.securityService = securityService;
    }

    public List<PostDTO> findAll() {
        log.debug("Fetching all posts");
        Long currentUserId = securityService.getCurrentUserId();
        return postRepository.findAll().stream()
                .map(post -> postMapper.toDto(post, currentUserId, postLikeRepository, commentRepository))
                .collect(Collectors.toList());
    }

    public PostDTO findById(Long id) {
        log.debug("Fetching post by ID: {}", id);
        Long currentUserId = securityService.getCurrentUserId();
        return postRepository.findById(id)
                .map(post -> postMapper.toDto(post, currentUserId, postLikeRepository, commentRepository))
                .orElseThrow(() -> {
                    log.error("Post not found with ID: {}", id);
                    return new NotFoundException("Post not found");
                });
    }

    public void validatePostOwner(Long postId) {
        PostDTO post = findById(postId);
        if (!securityService.isOwner(post.getAuthor().id())) {
            log.warn("Unauthorized access attempt to post {}", postId);
            throw new AccessDeniedException("You are not the owner of this post");
        }
    }

    @Transactional
    public PostDTO create(String authorUsername, String content, Long categoryId, List<MultipartFile> images) {
        log.info("Creating new post by user: {}", authorUsername);

        User author = userRepository.findByUsernameIgnoreCase(authorUsername)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        Post post = new Post();
        post.setContent(content);
        post.setAuthor(author);
        post.setCategory(category);

        List<String> imageUrls = saveImages(images);
        post.setImages(imageUrls);

        Post saved = postRepository.save(post);
        Long currentUserId = author.getId();
        return postMapper.toDto(saved, currentUserId, postLikeRepository, commentRepository);
    }

    @Transactional
    public PostDTO update(Long id, PostCreateDTO dto) {
        log.info("Updating post ID: {}", id);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        post.setContent(dto.content());
        post.setCategory(category);
        post.setImages(dto.images() == null ? List.of() : dto.images());

        Post updated = postRepository.save(post);
        Long currentUserId = securityService.getCurrentUserId();
        return postMapper.toDto(updated, currentUserId, postLikeRepository, commentRepository);
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

    private List<String> saveImages(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) return List.of();

        List<String> urls = new ArrayList<>();
        for (MultipartFile image : images) {
            try {
                String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
                Path uploadPath = Paths.get("uploads");

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Path filePath = uploadPath.resolve(filename);
                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/uploads/")
                        .path(filename)
                        .toUriString();

                urls.add(fileUrl);
            } catch (IOException e) {
                log.error("Failed to save image", e);
            }
        }

        return urls;
    }
}

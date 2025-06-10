package org.example.stashroom.controllers;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.stashroom.dto.PostCreateDTO;
import org.example.stashroom.dto.PostDTO;
import org.example.stashroom.services.PostService;
import org.example.stashroom.services.SecurityService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final SecurityService securityService;

    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        List<PostDTO> posts = postService.findAll();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
        PostDTO post = postService.findById(id);
        return ResponseEntity.ok(post);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDTO> createPost(
            @RequestParam("content") String content,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "images", required = false) List<MultipartFile> images
    ) {
        String username = securityService.getCurrentUserUsername();
        PostDTO createdPost = postService.create(username, content, categoryId, images);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdPost.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdPost);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostCreateDTO dto) {
        postService.validatePostOwner(id);
        PostDTO updatedPost = postService.update(id, dto);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.validatePostOwner(id);
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

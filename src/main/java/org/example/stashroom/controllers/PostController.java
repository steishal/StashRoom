package org.example.stashroom.controllers;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.stashroom.dto.PostCreateDTO;
import org.example.stashroom.dto.PostDTO;
import org.example.stashroom.services.PostService;
import org.example.stashroom.services.SecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping
    public ResponseEntity<PostDTO> createPost(@Valid @RequestBody PostCreateDTO dto) {
        String username = securityService.getCurrentUserUsername();
        PostDTO createdPost = postService.create(username, dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdPost.id())
                .toUri();

        return ResponseEntity.created(location).body(createdPost);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostCreateDTO dto) {
        securityService.validatePostOwner(id);
        PostDTO updatedPost = postService.update(id, dto);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        securityService.validatePostOwner(id);
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

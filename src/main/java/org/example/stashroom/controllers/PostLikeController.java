package org.example.stashroom.controllers;
import lombok.RequiredArgsConstructor;
import org.example.stashroom.dto.PostLikeDTO;
import org.example.stashroom.services.PostLikeService;
import org.example.stashroom.services.SecurityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/likes")
@RequiredArgsConstructor
public class PostLikeController {
    private final PostLikeService postLikeService;
    private final SecurityService securityService;

    @GetMapping
    public ResponseEntity<List<PostLikeDTO>> getPostLikes(@PathVariable Long postId) {
        List<PostLikeDTO> likes = postLikeService.findLikesByPost(postId);
        return ResponseEntity.ok(likes);
    }

    @PostMapping
    public ResponseEntity<Void> addLike(@PathVariable Long postId) {
        Long userId = securityService.getCurrentUserId();
        postLikeService.like(postId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removeLike(@PathVariable Long postId) {
        Long userId = securityService.getCurrentUserId();
        postLikeService.unlike(postId, userId);
        return ResponseEntity.noContent().build();
    }
}

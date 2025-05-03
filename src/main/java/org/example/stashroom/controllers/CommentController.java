package org.example.stashroom.controllers;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.stashroom.dto.CommentCreateDTO;
import org.example.stashroom.dto.CommentDTO;
import org.example.stashroom.services.CommentService;
import org.example.stashroom.services.SecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final SecurityService securityService;

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByPost(@PathVariable Long postId) {
        List<CommentDTO> comments = commentService.findByPost(postId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@Valid @RequestBody CommentCreateDTO dto) {
        String username = securityService.getCurrentUserUsername();
        CommentDTO createdComment = commentService.create(username, dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdComment.id())
                .toUri();

        return ResponseEntity.created(location).body(createdComment);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable String commentId,
            @Valid @RequestBody CommentCreateDTO dto) {
        String username = securityService.getCurrentUserUsername();
        CommentDTO updatedComment = commentService.update(commentId, username, dto);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable String commentId) {
        commentService.delete(commentId);
        return ResponseEntity.noContent().build();
    }
}

package org.example.stashroom.controllers;
import lombok.RequiredArgsConstructor;
import org.example.stashroom.dto.FollowerDTO;
import org.example.stashroom.services.FollowerService;
import org.example.stashroom.services.SecurityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FollowerController {
    private final FollowerService followerService;
    private final SecurityService securityService;

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<FollowerDTO>> getFollowers(@PathVariable Long userId) {
        List<FollowerDTO> followers = followerService.findFollowersOf(userId);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<FollowerDTO>> getFollowing(@PathVariable Long userId) {
        List<FollowerDTO> following = followerService.findFollowingOf(userId);
        return ResponseEntity.ok(following);
    }

    @PostMapping("/{followingId}/follow")
    public ResponseEntity<Void> followUser(@PathVariable Long followingId) {
        Long followerId = securityService.getCurrentUserId();
        followerService.follow(followerId, followingId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{followingId}/follow")
    public ResponseEntity<Void> unfollowUser(@PathVariable Long followingId) {
        Long followerId = securityService.getCurrentUserId();
        followerService.unfollow(followerId, followingId);
        return ResponseEntity.noContent().build();
    }
}

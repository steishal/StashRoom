package org.example.stashroom.services;
import lombok.extern.slf4j.Slf4j;
import org.example.stashroom.dto.FollowerDTO;
import org.example.stashroom.entities.Follower;
import org.example.stashroom.entities.User;
import org.example.stashroom.exceptions.DuplicateEntityException;
import org.example.stashroom.exceptions.NotFoundException;
import org.example.stashroom.mappers.FollowerMapper;
import org.example.stashroom.repositories.FollowerRepository;
import org.example.stashroom.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
public class FollowerService {
    private final FollowerRepository followerRepository;
    private final UserRepository userRepository;
    private final FollowerMapper followerMapper;

    @Autowired
    public FollowerService(FollowerRepository followerRepository,
                           UserRepository userRepository,
                           FollowerMapper followerMapper) {
        this.followerRepository = followerRepository;
        this.userRepository = userRepository;
        this.followerMapper = followerMapper;
    }

    public List<FollowerDTO> findFollowersOf(Long userId) {
        log.debug("Fetching followers for user ID: {}", userId);
        return followerRepository.findByFollowing_Id(userId).stream()
                .map(followerMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<FollowerDTO> findFollowingOf(Long userId) {
        log.debug("Fetching following for user ID: {}", userId);
        return followerRepository.findByFollower_Id(userId).stream()
                .map(followerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void follow(Long followerId, Long followingId) {
        log.info("User {} trying to follow user {}", followerId, followingId);

        if (followerRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            log.error("Follow relationship already exists");
            throw new DuplicateEntityException("Already following");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> {
                    log.error("Follower not found: {}", followerId);
                    return new NotFoundException("Follower not found");
                });

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> {
                    log.error("Following user not found: {}", followingId);
                    return new NotFoundException("Following user not found");
                });

        Follower f = new Follower();
        f.setFollower(follower);
        f.setFollowing(following);
        followerRepository.save(f);
        log.info("User {} successfully followed user {}", followerId, followingId);
    }

    @Transactional
    public void unfollow(Long followerId, Long followingId) {
        log.info("User {} unfollowing user {}", followerId, followingId);
        if (!followerRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            log.warn("No follow relationship to delete");
            return;
        }
        followerRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
        log.debug("Follow relationship deleted");
    }
}

package org.example.stashroom.services;
import org.example.stashroom.dto.FollowerDTO;
import org.example.stashroom.entities.Follower;
import org.example.stashroom.entities.User;
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
public class FollowerService {
    @Autowired private FollowerRepository followerRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private FollowerMapper followerMapper;

    public List<FollowerDTO> findFollowersOf(Long userId) {
        return followerRepository.findByFollowing_Id(userId).stream()
                .map(followerMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<FollowerDTO> findFollowingOf(Long userId) {
        return followerRepository.findByFollower_Id(userId).stream()
                .map(followerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void follow(Long followerId, Long followingId) {
        if (followerRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new RuntimeException("Already following");
        }
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Follower f = new Follower();
        f.setFollower(follower);
        f.setFollowing(following);
        followerRepository.save(f);
    }

    @Transactional
    public void unfollow(Long followerId, Long followingId) {
        followerRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
    }
}

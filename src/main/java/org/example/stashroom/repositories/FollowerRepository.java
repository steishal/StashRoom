package org.example.stashroom.repositories;
import org.example.stashroom.entities.Follower;
import org.example.stashroom.entities.FollowerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowerRepository extends JpaRepository<Follower, FollowerId> {
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);
    List<Follower> findByFollowing_Id(Long followingId);
    List<Follower> findByFollower_Id(Long followerId);
    @Query("SELECT COUNT(f) FROM Follower f WHERE f.following.id = :userId")
    int countFollowers(@Param("userId") Long userId);
    @Query("SELECT COUNT(f) FROM Follower f WHERE f.follower.id = :userId")
    int countFollowing(@Param("userId") Long userId);
}
package org.example.stashroom.entities;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@IdClass(FollowerId.class)
public class Follower {
    @Id
    @ManyToOne
    @JoinColumn(name = "follower_id")
    private User follower;
    @Id
    @ManyToOne
    @JoinColumn(name = "following_id")
    private User following;
}

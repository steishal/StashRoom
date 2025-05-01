package org.example.stashroom.entities;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
public class FollowerId implements Serializable {
    private Long follower;
    private Long following;
    public FollowerId() {
    }
    public FollowerId(Long follower, Long following) {
        this.follower = follower;
        this.following = following;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FollowerId that = (FollowerId) o;
        return Objects.equals(follower, that.follower) &&
                Objects.equals(following, that.following);
    }
    @Override
    public int hashCode() {
        return Objects.hash(follower, following);
    }
}

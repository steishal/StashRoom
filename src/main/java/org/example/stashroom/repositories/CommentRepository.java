package org.example.stashroom.repositories;
import org.example.stashroom.entities.Comment;
import org.example.stashroom.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, String> {
    List<Comment> findByPostId(Long postId);
    int countByPost(Post post);
    int countByPostId(Long postId);
    void deleteAllByPost(Post post);
}

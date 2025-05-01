package org.example.stashroom.repositories;
import org.example.stashroom.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {}
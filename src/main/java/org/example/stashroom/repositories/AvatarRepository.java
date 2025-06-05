package org.example.stashroom.repositories;
import org.example.stashroom.entities.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {}

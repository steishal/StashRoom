package org.example.stashroom.repositories;
import org.example.stashroom.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameIgnoreCase(String username);
    boolean existsByEmail(String email);
    @Query("SELECT u FROM User u WHERE u.vkLink = :link OR u.tgLink = :link")
    List<User> findBySocialLink(@Param("link") String link);
    boolean existsByPhoneNumber(String phoneNumber);
}
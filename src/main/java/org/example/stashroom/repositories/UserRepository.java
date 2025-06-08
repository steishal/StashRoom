package org.example.stashroom.repositories;
import org.example.stashroom.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByUsernameIgnoreCase(String username);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.phoneNumber = :phone")
    User findByEmailAndPhone(@Param("email") String email, @Param("phone") String phone);
    List<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
    @Query("SELECT u.telegramChatId FROM User u WHERE u.id = :userId")
    String findTelegramChatIdByUserId(@Param("userId") Long userId);
    Optional<User> findByTelegramLinkToken(String token);
}

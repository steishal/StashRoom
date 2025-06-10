package org.example.stashroom.repositories;
import org.example.stashroom.entities.Message;
import org.example.stashroom.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender.id = :userA AND m.receiver.id = :userB) OR " +
            "(m.sender.id = :userB AND m.receiver.id = :userA) " +
            "ORDER BY m.sentAt ASC")
    List<Message> findConversation(@Param("userA") Long userA, @Param("userB") Long userB);

    @Query("SELECT " +
            "CASE WHEN m.sender.id = :userId THEN m.receiver.id ELSE m.sender.id END, " +
            "CASE WHEN m.sender.id = :userId THEN m.receiver.username ELSE m.sender.username END, " +
            "m.content, " +
            "m.sentAt " +
            "FROM Message m " +
            "WHERE m.sender.id = :userId OR m.receiver.id = :userId " +
            "ORDER BY m.sentAt DESC")
    List<Object[]> findChatsForUser(@Param("userId") Long userId);

    @Query("""
    SELECT m FROM Message m 
    WHERE (m.sender.id = :user1 AND m.receiver.id = :user2)
       OR (m.sender.id = :user2 AND m.receiver.id = :user1)
    ORDER BY m.sentAt DESC
    LIMIT 1
""")
    Message findLatestMessageBetween(@Param("user1") Long user1, @Param("user2") Long user2);
    @Query("""
    SELECT m FROM Message m
    WHERE (m.sender = :user OR m.receiver = :user)
    AND m.sentAt = (
        SELECT MAX(m2.sentAt) FROM Message m2
        WHERE 
            (m2.sender = m.sender AND m2.receiver = m.receiver)
            OR (m2.sender = m.receiver AND m2.receiver = m.sender)
    )
""")
    List<Message> findLastMessagesForEachChat(@Param("user") User user);

}

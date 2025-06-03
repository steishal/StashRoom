package org.example.stashroom.repositories;
import org.example.stashroom.entities.Message;
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
}

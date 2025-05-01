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
}
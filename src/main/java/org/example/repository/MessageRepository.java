package org.example.repository;

import org.example.model.Message;
import java.util.List;
import java.util.Optional;

import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // 1. Find all messages between two users (both directions, ordered by timestamp)
    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender.id = :user1 AND m.recipient.id = :user2) OR " +
            "(m.sender.id = :user2 AND m.recipient.id = :user1) " +
            "ORDER BY m.timestamp ASC")
    List<Message> findConversation(@Param("user1") Long user1, @Param("user2") Long user2);


    // 2. Find unread messages for a user
    //List<Message> findByRecipientIdAndReadFalse(Long recipientId);

    // 3. Save and update methods inherited from JpaRepository — no need to declare explicitly

    // (Optional) Custom bulk update to mark messages as read — example if needed
    /*
    @Modifying
    @Query("UPDATE Message m SET m.read = true WHERE m.recipient.id = :userId AND m.read = false")
    int markMessagesAsRead(@Param("userId") Long userId);
    */
}

package org.example.repository;

import org.example.model.Message;
import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // NEW: Method to count unread messages for a specific user.
    // The query now checks the recipient object.
    long countByRecipientIdAndIsReadFalse(Long recipientId);


    // NEW: Method to mark messages from a specific sender to a recipient as read.
    @Transactional
    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.recipient = :recipient AND m.sender = :sender AND m.isRead = false")
    int markMessagesAsReadBySenderAndRecipient(
            @Param("sender") User sender,
            @Param("recipient") User recipient
    );

    @Query("SELECT m FROM Message m WHERE (m.sender.id = :user1 AND m.recipient.id = :user2) OR (m.sender.id = :user2 AND m.recipient.id = :user1) ORDER BY m.timestamp ASC")
    List<Message> findChatHistory(@Param("user1") Long user1, @Param("user2") Long user2);


    List<Message> findTop3ByRecipientOrderByTimestampDesc(User recipient);

}
package org.example.repository;

import org.example.model.Connection;
import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConnectionRepository extends JpaRepository <Connection, Long> {
    // finding connections by sender and receiver
    Optional<Connection> findBySenderAndReceiver(User sender, User receiver);

    // Find all pending requests for a user (where they are the receiver)

    List<Connection> findByReceiverAndStatus(User receiver, Connection.ConnectionStatus status);

    // Find all accepted connections for a user
    List<Connection> findBySenderAndStatusOrReceiverAndStatus(User sender, Connection.ConnectionStatus status1, User receiver, Connection.ConnectionStatus status2);

    long countBySenderAndStatus(User sender, Connection.ConnectionStatus status);

    // New: Count users who are following a given user
    // Spring Data JPA derives a COUNT(*) query for all connections where 'receiver' is the given User
    // AND the 'status' is ACCEPTED.
    long countByReceiverAndStatus(User receiver, Connection.ConnectionStatus status);

    Optional<Connection> findBySenderAndReceiverOrReceiverAndSender(User user1, User user2, User user3, User user4);

    // ... other existing methods (countBySenderAndStatus, countByReceiverAndStatus, etc.)
}


package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.Connection;
import org.example.model.User;
import org.example.repository.ConnectionRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ConnectionService {
    private final ConnectionRepository connectionRepository;
    private final UserRepository userRepository;

    public Connection sendFriendRequest(Long senderId, Long receiverId) {
        User sender = userRepository.findById(senderId).orElseThrow(() -> new IllegalArgumentException("Sender not found."));
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new IllegalArgumentException("Receiver not found."));

        if (connectionRepository.findBySenderAndReceiver(sender, receiver).isPresent() ||
                connectionRepository.findBySenderAndReceiver(receiver, sender).isPresent()) {
            throw new IllegalStateException("Friend request already exists.");
        }
            Connection connection = new Connection();
            connection.setSender(sender);
            connection.setReceiver(receiver);
            connection.setStatus(Connection.ConnectionStatus.PENDING);
            connection.setCreatedAt(LocalDateTime.now());
            return connectionRepository.save(connection);
        }
    public Connection acceptFriendRequest(Long requestId) {
        Connection connection = connectionRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found."));

        if (connection.getStatus() != Connection.ConnectionStatus.PENDING) {
            throw new IllegalStateException("Request is not pending.");
        }
        connection.setStatus(Connection.ConnectionStatus.ACCEPTED);
        return connectionRepository.save(connection);
    }
    public List<Connection> getPendingRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        return connectionRepository.findByReceiverAndStatus(user, Connection.ConnectionStatus.PENDING);
    }

    public Connection rejectFriendRequest(Long requestId){
        Connection connection = connectionRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Connection request not found."));

        // 2. Check if the connection is in a PENDING state. If not, it can't be rejected.
        if (connection.getStatus() != Connection.ConnectionStatus.PENDING) {
            throw new IllegalStateException("Cannot reject a non-pending request.");
        }

        // 3. Update the status to REJECTED.
        connection.setStatus(Connection.ConnectionStatus.REJECTED);

        // 4. Save the updated connection back to the database.
        return connectionRepository.save(connection);
    }

}




package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.CreateUserDTO;
import org.example.model.Connection;
import org.example.model.ConnectionRelationStatus;
import org.example.model.Role;
import org.example.model.User;
import org.example.repository.ConnectionRepository;
import org.example.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.example.dto.UserResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime; // Added for completeness if createdAt/updatedAt are used
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service // Marks this class as a Spring service component
@Transactional // Ensures methods are executed within a transaction
@RequiredArgsConstructor // Lombok: Creates a constructor for final fields (UserRepository, BCryptPasswordEncoder)
public class UserService {

    private final UserRepository userRepository; // Injected by Lombok's @RequiredArgsConstructor
    private final BCryptPasswordEncoder passwordEncoder;
    private final ConnectionRepository connectionRepository;// Injected BCryptPasswordEncoder

    public User registerNewUser(CreateUserDTO createUserDTO) {
        // Business logic: Check if email already exists
        // Assuming findByEmail is available in UserRepository
        if (userRepository.findByEmail(createUserDTO.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists: " + createUserDTO.getEmail());
        }

        User newUser = new User();
        newUser.setName(createUserDTO.getName());
        newUser.setEmail(createUserDTO.getEmail());
        // !!! CRITICAL FIX: HASH THE PASSWORD BEFORE SAVING !!!
        newUser.setPassword(passwordEncoder.encode(createUserDTO.getPassword())); // <--- THIS IS THE REQUIRED CHANGE
        newUser.setDesignation(createUserDTO.getDesignation()); // Set the designation
        newUser.setRole(Role.USER); // Assign default role to USER

        // Assuming your User entity has createdAt and updatedAt fields
        // If not, you might need to add them or handle their population differently
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(newUser); // Save the new user to the database
    }

    public UserResponseDTO getUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();
        UserResponseDTO userResponseDTO = new UserResponseDTO(user);

        // Corrected: Call the helper method here!
        populateFollowerCounts(user, userResponseDTO);

        return userResponseDTO; // If not found, return null (controller will handle 404)
    }

    public UserResponseDTO authenticateUser(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            // Provide a generic error message for security reasons
            throw new RuntimeException("Invalid credentials.");
        }

        User user = userOptional.get();

        // Compare the provided raw password with the HASHED password from the database
        // This line is correct *assuming* the password was hashed during registration.
        if (!passwordEncoder.matches(password, user.getPassword())) {
            // Provide a generic error message for security reasons
            throw new RuntimeException("Invalid credentials.");
        }

        // Authentication successful, return the user details as DTO
        return new UserResponseDTO(user);
    }
    public List<UserResponseDTO> searchUsersByName(String query, Long requesterId) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("Requester user not found."));

        List<User> users = userRepository.searchByNameOrEmail(query);

        return users.stream()
                .filter(user -> !user.getId().equals(requester.getId())) // Don't show the requester in their own search results
                .map(user -> {
                    UserResponseDTO dto = new UserResponseDTO(user);
                    populateFollowerCounts(user, dto); // Populate general counts

                    // Determine connection status with the requesting user
                    Optional<Connection> connectionOptional = connectionRepository
                            .findBySenderAndReceiverOrReceiverAndSender(requester, user, requester, user);

                    if (connectionOptional.isPresent()) {
                        Connection connection = connectionOptional.get();
                        if (connection.getStatus() == Connection.ConnectionStatus.ACCEPTED) {
                            dto.setConnectionStatusWithRequester(ConnectionRelationStatus.ACCEPTED);
                            dto.setPendingRequestId(null);
                        } else if (connection.getStatus() == Connection.ConnectionStatus.PENDING) {
                            if (connection.getSender().getId().equals(requester.getId())) {
                                dto.setConnectionStatusWithRequester(ConnectionRelationStatus.PENDING_SENT);
                                dto.setPendingRequestId(null);
                            } else {
                                dto.setConnectionStatusWithRequester(ConnectionRelationStatus.PENDING_RECEIVED);
                                dto.setPendingRequestId(connection.getId());
                            }
                        }
                        // Add logic for REJECTED, BLOCKED if needed
                    } else {
                        dto.setConnectionStatusWithRequester(ConnectionRelationStatus.NOT_CONNECTED);
                        dto.setPendingRequestId(null);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void populateFollowerCounts(User user, UserResponseDTO dto){
        // A user's 'following' count is the number of connections they SENT
        long actualFollowingCount = connectionRepository.countBySenderAndStatus(user, Connection.ConnectionStatus.ACCEPTED);

// A user's 'followers' count is the number of connections they RECEIVED
        long actualFollowersCount = connectionRepository.countByReceiverAndStatus(user, Connection.ConnectionStatus.ACCEPTED);

        dto.setFollowingCount(actualFollowingCount); // Now correctly sets 'following'
        dto.setFollowersCount(actualFollowersCount); // Now correctly sets 'followers'

    }
}
package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.Role;
import org.example.model.User;
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
    private final BCryptPasswordEncoder passwordEncoder; // Injected BCryptPasswordEncoder

    public User registerNewUser(String name, String email, String password, String designation) {
        // Business logic: Check if email already exists
        // Assuming findByEmail is available in UserRepository
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("User with this email already exists: " + email);
        }

        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        // !!! CRITICAL FIX: HASH THE PASSWORD BEFORE SAVING !!!
        newUser.setPassword(passwordEncoder.encode(password)); // <--- THIS IS THE REQUIRED CHANGE
        newUser.setDesignation(designation); // Set the designation
        newUser.setRole(Role.USER); // Assign default role to USER

        // Assuming your User entity has createdAt and updatedAt fields
        // If not, you might need to add them or handle their population differently
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(newUser); // Save the new user to the database
    }

    public UserResponseDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserResponseDTO::new) // If User found, convert to DTO
                .orElse(null); // If not found, return null (controller will handle 404)
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
    public List<UserResponseDTO> searchUsersByName(String query) {
        List<User> users = userRepository.searchByNameOrEmail(query);
        return users.stream().map(UserResponseDTO::new).collect(Collectors.toList());
    }
}
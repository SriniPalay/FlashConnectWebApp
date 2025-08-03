package org.example.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.model.User;
import org.example.model.Role;

import java.time.LocalDateTime;

@Getter // Lombok: Generates getters
@Setter // Lombok: Generates setters
@NoArgsConstructor // Lombok: Generates a no-argument constructor
public class UserResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String designation; // Include designation in response
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor to convert a User entity to a UserResponseDTO
    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.designation = user.getDesignation(); // Map designation
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }
}
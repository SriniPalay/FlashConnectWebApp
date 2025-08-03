package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore; // Import for password
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity // Marks this class as a JPA entity
@Table(name = "users") // Specifies the table name in the database
@Getter // Lombok: Generates getters for all fields
@Setter // Lombok: Generates setters for all fields
@NoArgsConstructor // Lombok: Generates a no-argument constructor
@ToString(exclude = {"password"}) // Lombok: Generates toString, excluding password for security
public class User {
    @Id // Marks id as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increments ID for PostgreSQL
    private Long id;

    @Column(nullable = false) // Column cannot be null
    private String name;

    @Column(nullable = false, unique = true) // Column cannot be null and must be unique
    private String email;

    @Column(nullable = false)
    @JsonIgnore // Important: Prevents password from being serialized to JSON responses
    private String password;

    @Column(nullable = false)
    private String designation; // New field for user's designation

    @Enumerated(EnumType.STRING) // Stores enum as String in DB ("USER", "ADMIN")
    @Column(nullable = false)
    private Role role = Role.USER; // Default role for new users

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // JPA lifecycle callbacks to set timestamps automatically
    @PrePersist // Called before the entity is first persisted (inserted)
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate // Called before the entity is updated
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
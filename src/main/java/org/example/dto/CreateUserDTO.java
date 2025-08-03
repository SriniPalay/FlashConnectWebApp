package org.example.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
// import jakarta.validation.constraints.Email; // Optional: Uncomment if you add validation later
// import jakarta.validation.constraints.NotBlank;

@Getter // Lombok: Generates getters
@Setter // Lombok: Generates setters
@NoArgsConstructor // Lombok: Generates a no-argument constructor
public class CreateUserDTO {
    // @NotBlank(message = "Name cannot be blank") // Example validation annotation
    private String name;

    // @NotBlank(message = "Email cannot be blank")
    // @Email(message = "Invalid email format") // Example validation annotation
    private String email;

    // @NotBlank(message = "Password cannot be blank")
    private String password; // Raw password from frontend

    // New field from frontend
    // @NotBlank(message = "Designation cannot be blank")
    private String designation;
}
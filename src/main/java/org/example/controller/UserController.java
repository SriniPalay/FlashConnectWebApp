package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.CreateUserDTO;
import org.example.controller.ErrorResponse;
import org.example.dto.LoginRequestDTO;
import org.example.dto.UserResponseDTO;
import org.example.model.User;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Marks this class as a REST controller
@RequestMapping("/users") // Base path for all endpoints in this controller: /api/users
@RequiredArgsConstructor // Lombok: Injects UserService via constructor
@CrossOrigin(origins = "*") // Allows requests from any origin (for frontend development)
public class UserController {


    private final UserService userService; // Injected service

    @PostMapping("/register") // Maps POST requests to /api/users/register
    public ResponseEntity<?> registerUser(@RequestBody CreateUserDTO createUserDTO) {
        try {
            // Call the service layer to register the new user
            User createdUser = userService.registerNewUser(
                    createUserDTO.getName(),
                    createUserDTO.getEmail(),
                    createUserDTO.getPassword(),
                    createUserDTO.getDesignation() // Pass designation from DTO
            );

            // Return a UserResponseDTO to the client (excludes sensitive info like password)
            return new ResponseEntity<>(new UserResponseDTO(createdUser), HttpStatus.CREATED); // 201 Created
        } catch (RuntimeException e) {
            // Catch specific exceptions (e.g., email already exists)
            return new ResponseEntity<>(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST); // 400 Bad Request
        } catch (Exception e) {
            // Catch any other unexpected errors
            return new ResponseEntity<>(new ErrorResponse("An unexpected error occurred during registration.", HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }

    @GetMapping("/{id}") // Maps GET requests to /users/{id}
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        // Assume userService.getUserById returns UserResponseDTO directly or an Optional<User>
        // If it returns Optional<User>, you'd map it to UserResponseDTO here.
        // For simplicity, let's assume it returns UserResponseDTO or null/throws if not found.
        UserResponseDTO userDto = userService.getUserById(id); // <-- This method needs to exist in UserService

        if (userDto != null) {
            return ResponseEntity.ok(userDto); // 200 OK
        } else {
            // This is the correct way to return a 404 from your application logic
            return ResponseEntity.notFound().build(); // 404 Not Found (user not found)
        }
    }

    @PostMapping("/login") // Maps POST requests to /users/login
    public ResponseEntity<?> signInUser(@RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            UserResponseDTO userDto = userService.authenticateUser(
                    loginRequestDTO.getEmail(),
                    loginRequestDTO.getPassword()
            );
            // If authentication is successful, return user details
            return ResponseEntity.ok(userDto); // 200 OK
        } catch (RuntimeException e) {
            // Handle authentication failures (e.g., "Invalid credentials")
            return new ResponseEntity<>(new ErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED.value()), HttpStatus.UNAUTHORIZED); // 401 Unauthorized
        } catch (Exception e) {
            // Catch any other unexpected errors
            return new ResponseEntity<>(new ErrorResponse("An unexpected error occurred during login.", HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }

    @GetMapping("/search") // Maps HTTP GET requests to "/api/users/search"
    public ResponseEntity<List<UserResponseDTO>> searchUsers(
            @RequestParam("query") String query, // Extracts 'query' parameter from URL (e.g., ?query=John)
            @RequestParam("requesterId") Long requesterId) { // Extracts 'requesterId' parameter (e.g., &requesterId=123)
        try {
            // 1. Delegate to UserService:
            // Calls the UserService to perform the search.
            // Importantly, it passes BOTH the search 'query' AND the 'requesterId'.
            // The UserService will use 'requesterId' to determine the connection status
            // between the searching user and each user found in the search results.
            List<UserResponseDTO> users = userService.searchUsersByName(query, requesterId);

            // 2. Return Success Response:
            // If the search is successful, it returns a 200 OK status.
            // The 'users' list (which now contains connection statuses within each DTO)
            // is sent as the JSON body of the response.
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            // 3. Handle 'Requester Not Found' Error:
            // If the 'requesterId' itself is invalid (user doesn't exist),
            // the UserService throws an IllegalArgumentException.
            // The controller catches it and returns a 404 NOT FOUND status.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception ex) {
            // 4. Handle Generic Server Errors:
            // Catches any other unexpected exceptions during the search process.
            // Logs the error to the console for debugging.
            // Returns a 500 INTERNAL SERVER ERROR status, indicating a server-side problem.
            System.err.println("An error occurred while searching for users: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // Returns an empty body for generic error
        }
    }

}
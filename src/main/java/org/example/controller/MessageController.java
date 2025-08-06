package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.MessageRequestDTO;
import org.example.dto.MessageResponseDTO;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.service.MessageService;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path="/messages") // Corrected path to include /api
@CrossOrigin(origins = "*")
@RequiredArgsConstructor // This generates the constructor for the final fields below
public class MessageController {

    // These fields must be 'final' for @RequiredArgsConstructor to work
    private final MessageService messageService;
    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/sendMessages")
    public ResponseEntity<MessageResponseDTO> sendMessage(@RequestBody MessageRequestDTO dto) {
        try {
            MessageResponseDTO response = messageService.sendMessage(dto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // A RESTful endpoint for getting the unread count for a specific user
    @GetMapping("/unreadCount/{userId}")
    public ResponseEntity<Map<String, Long>> getUnreadMessageCount(@PathVariable Long userId) {
        long unreadCount = messageService.getUnreadMessageCountForUser(userId);
        return ResponseEntity.ok(Map.of("unreadCount", unreadCount));
    }

    // The previous unreadCount endpoint is redundant and can be removed
    // @GetMapping("/unread/count")
    // public long getUnreadCount(@RequestParam("userId") Long userId) {
    //     return messageService.getUnreadMessageCount(userId);
    // }

    @GetMapping("/chat")
    public ResponseEntity<List<MessageResponseDTO>> getChatHistory(
            @RequestParam("user1") Long user1Id,
            @RequestParam("user2") Long user2Id) {
        List<MessageResponseDTO> chatHistory = messageService.getChatHistory(user1Id, user2Id);
        return ResponseEntity.ok(chatHistory);
    }

    @GetMapping("/notifications/{userId}")
    public ResponseEntity<List<MessageResponseDTO>> getRecentNotifications(@PathVariable Long userId) {

        // Use the UserRepository directly to get the User entity
        Optional<User> recipientUser = userRepository.findById(userId);

        if (recipientUser.isPresent()) {
            List<MessageResponseDTO> notifications = messageService.getRecentNotifications(recipientUser.get());
            return ResponseEntity.ok(notifications);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
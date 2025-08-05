package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.MessageRequestDTO;
import org.example.dto.MessageResponseDTO;
import org.example.service.MessageService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path="/messages")
@CrossOrigin(origins = "*") // allow cross-origin during development
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;

    @PostMapping("/sendMessages")
    public ResponseEntity<MessageResponseDTO> sendMessage(@RequestBody MessageRequestDTO dto) {
        try {
            MessageResponseDTO response = messageService.sendMessage(dto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    @GetMapping("/unread/count")
    public long getUnreadCount(@RequestParam("userId") Long userId) {
        return messageService.getUnreadMessageCount(userId);
    }
    @GetMapping("/chat")
    public ResponseEntity<List<MessageResponseDTO>> getChatHistory(
            @RequestParam("user1") Long user1Id,
            @RequestParam("user2") Long user2Id) {
        List<MessageResponseDTO> chatHistory = messageService.getChatHistory(user1Id, user2Id);
        return ResponseEntity.ok(chatHistory);
    }
    @GetMapping("/unreadCount/{userId}")
    public ResponseEntity<Map<String, Long>> getUnreadMessageCount(@PathVariable Long userId) {
        // The controller now calls the service method you just created
        long unreadCount = messageService.getUnreadMessageCountForUser(userId);
        return ResponseEntity.ok(Map.of("unreadCount", unreadCount));
    }
    }

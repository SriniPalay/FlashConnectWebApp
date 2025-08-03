package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.MessageRequestDTO;
import org.example.dto.MessageResponseDTO;
import org.example.service.MessageService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}

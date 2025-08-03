package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.MessageRequestDTO;
import org.example.dto.MessageResponseDTO;
import org.example.model.Message;
import org.example.model.User;
import org.example.repository.MessageRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository; // To fetch User entities

    public MessageResponseDTO sendMessage(MessageRequestDTO dto) {
        // Fetch sender and recipient User entities from DB
        User sender = userRepository.findById(dto.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid sender ID"));
        User recipient = userRepository.findById(dto.getRecipientId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid recipient ID"));

        // Create message entity
        Message message = new Message();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setContent(dto.getContent());
        message.setTimestamp(LocalDateTime.now());
        // We can ignore read flag for now or set false by default

        // Save the message
        Message saved = messageRepository.save(message);

        // Convert to response DTO
        return new MessageResponseDTO(saved);
    }
}

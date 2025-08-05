package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.MessageRequestDTO;
import org.example.dto.MessageResponseDTO;
import org.example.model.Message;
import org.example.model.User;
import org.example.repository.MessageRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    @Autowired
    private final MessageRepository messageRepository;
    @Autowired
    private final UserRepository userRepository;// To fetch User entities

    private MessageResponseDTO convertToResponseDTO(Message message) {
        MessageResponseDTO dto = new MessageResponseDTO();
        dto.setId(message.getId());
        dto.setSenderId(message.getSender().getId());
        dto.setRecipientId(message.getRecipient().getId());
        dto.setContent(message.getContent());
        dto.setTimestamp(message.getTimestamp());
        return dto;
    }

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
    public long getUnreadMessageCount(Long recipientId) {
        return messageRepository.countByRecipientIdAndIsReadFalse(recipientId);
    }
    public List<MessageResponseDTO> getChatHistory(Long user1Id, Long user2Id) {
        List<Message> messages = messageRepository.findChatHistory(user1Id, user2Id);
        return messages.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    public long getUnreadMessageCountForUser(Long userId) {
        return messageRepository.countByRecipientIdAndIsReadFalse(userId);
    }

}

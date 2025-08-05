package org.example.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.Message;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class MessageResponseDTO {
    private Long id;
    private Long senderId;
    private String senderName;
    private Long recipientId;
    private String content;
    private LocalDateTime timestamp;

    public MessageResponseDTO(Message message){
        this.id = message.getId();
        this.senderId = message.getSender().getId();
        this.senderName = message.getSender().getName();
        this.recipientId = message.getRecipient().getId();
        this.content = message.getContent();
        this.timestamp = message.getTimestamp();
    }
}
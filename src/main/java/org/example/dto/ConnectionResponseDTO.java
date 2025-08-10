package org.example.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.Connection;

@Data
@NoArgsConstructor
public class ConnectionResponseDTO {
    private Long id;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String receiverName;
    private Connection.ConnectionStatus status;

    public ConnectionResponseDTO(Connection connection) {
        this.id = connection.getId();
        this.senderId = connection.getSender().getId();
        this.senderName = connection.getSender().getName();
        this.receiverId = connection.getReceiver().getId();
        this.receiverName = connection.getReceiver().getName();
        this.status = connection.getStatus();
    }
}

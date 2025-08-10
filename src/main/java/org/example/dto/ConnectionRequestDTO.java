package org.example.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ConnectionRequestDTO {
    private Long senderId;
    private Long receiverId;
}


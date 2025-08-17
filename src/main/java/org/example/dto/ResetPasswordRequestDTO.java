package org.example.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResetPasswordRequestDTO {
    private String email; // NEW: To identify the user along with the code
    private String verificationCode; // RENAMED from 'token'
    private String newPassword;
}

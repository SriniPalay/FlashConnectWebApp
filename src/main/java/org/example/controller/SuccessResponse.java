// File: src/main/java/org/example/controller/SuccessResponse.java
// Or:   src/main/java/org/example/dto/SuccessResponse.java (recommended)

package org.example.controller; // Change to org.example.dto if you put it there

public class SuccessResponse {
    private String message;
    private long timestamp;

    public SuccessResponse(String message) {
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters
    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // Setters (if needed)
    public void setMessage(String message) {
        this.message = message;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
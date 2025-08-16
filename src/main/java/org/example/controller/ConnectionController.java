package org.example.controller;


import lombok.RequiredArgsConstructor;
import org.example.dto.ConnectionRequestDTO;
import org.example.dto.ConnectionResponseDTO;
import org.example.model.Connection;
import org.example.model.User;
import org.example.service.ConnectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/connections")
@RequiredArgsConstructor

public class ConnectionController {
    private final ConnectionService connectionService;

    @PostMapping("/send-request")
    public ResponseEntity<ConnectionResponseDTO> sendRequest(@RequestBody ConnectionRequestDTO dto){
        try{
            Connection newConnection = connectionService.sendFriendRequest(dto.getSenderId(), dto.getReceiverId());
            ConnectionResponseDTO responseDTO = new ConnectionResponseDTO(newConnection);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);

        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("/accept-request/{requestId}")
    public ResponseEntity<ConnectionResponseDTO> acceptRequest(@PathVariable Long requestId){
        try {
            Connection acceptedConnection = connectionService.acceptFriendRequest(requestId);
            ConnectionResponseDTO responseDTO = new ConnectionResponseDTO(acceptedConnection);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/pending/{userId}")
    public ResponseEntity<List<ConnectionResponseDTO>> getPendingRequests(@PathVariable Long userId) {
        try {
            List<ConnectionResponseDTO> pendingRequests = connectionService.getPendingRequests(userId)
                    .stream()
                    .map(ConnectionResponseDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(pendingRequests);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/reject/{userId}")
    public ResponseEntity<ConnectionResponseDTO> rejectRequest(@PathVariable Long userId){
        try{
            Connection rejectConnection = connectionService.rejectFriendRequest(userId);
            ConnectionResponseDTO connectionResponseDTO = new ConnectionResponseDTO(rejectConnection);
            return ResponseEntity.ok(connectionResponseDTO);
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}

package com.ayush.backendtraining.controller;

import com.ayush.backendtraining.dto.LeaveRequestDTO;
import com.ayush.backendtraining.dto.LeaveResponseDTO;
import com.ayush.backendtraining.dto.UpdateLeaveRequestDTO;
import com.ayush.backendtraining.service.LeaveService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/leaves")
public class LeaveController {

    private LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @GetMapping
    public ResponseEntity<List<LeaveResponseDTO>> getAllLeaves() {
        List<LeaveResponseDTO> response = leaveService.getAllLeaveRequest();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveResponseDTO> getLeaveById(@PathVariable Long id) {
        LeaveResponseDTO response = leaveService.getLeaveRequest(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<LeaveResponseDTO> createLeaveRequest(@Valid @RequestBody LeaveRequestDTO leaveRequestDTO) {
        LeaveResponseDTO responseDTO = leaveService.createLeaveRequest(leaveRequestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeaveResponseDTO> updateLeaveRequest(@PathVariable Long id, @Valid @RequestBody UpdateLeaveRequestDTO updateLeaveRequestDTO) {
        LeaveResponseDTO updatedResponse = leaveService.updateLeaveRequest(id, updateLeaveRequestDTO);
        return ResponseEntity.ok(updatedResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLeaveRequest(@PathVariable Long id) {
        return ResponseEntity.ok(leaveService.deleteLeaveRequest(id));
    }

}

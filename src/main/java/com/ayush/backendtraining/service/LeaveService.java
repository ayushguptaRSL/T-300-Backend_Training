package com.ayush.backendtraining.service;

import com.ayush.backendtraining.dto.LeaveRequestDTO;
import com.ayush.backendtraining.dto.LeaveResponseDTO;
import com.ayush.backendtraining.dto.UpdateLeaveRequestDTO;
import com.ayush.backendtraining.model.LeaveRequest;
import com.ayush.backendtraining.model.LeaveType;
import com.ayush.backendtraining.repository.LeaveRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LeaveService {

    private LeaveRepository leaveRepository;

    public LeaveService(LeaveRepository leaveRepository) {
        this.leaveRepository = leaveRepository;
    }

    public List<LeaveResponseDTO> getAllLeaveRequest(){
         List<LeaveRequest> allRequests = leaveRepository.findAllLeaveRequests();

         return allRequests.stream()
                 .map(this::mapLeaveRequestToResponseDTO)
                 .toList();
    }

    public LeaveResponseDTO getLeaveRequest(Long id) {
        LeaveRequest leaveRequest = leaveRepository.findLeaveRequestById(id).orElse(null);
        return mapLeaveRequestToResponseDTO(leaveRequest);
    }

    public LeaveResponseDTO createLeaveRequest(LeaveRequestDTO request) {
        LeaveRequest leaveRequest = leaveRepository.saveLeaveRequest(new LeaveRequest(
                request.getEmployeeId(),
                request.getLeaveType(),
                request.getStartDate(),
                request.getEndDate(),
                request.getReason()
        ));

        return mapLeaveRequestToResponseDTO(leaveRequest);
    }

    public LeaveResponseDTO updateLeaveRequest(Long id, UpdateLeaveRequestDTO updateRequest) {
        LeaveRequest leaveRequest = leaveRepository.findLeaveRequestById(id).orElse(null);
        if (leaveRequest == null) {
            return null;
        }

        if (updateRequest.getLeaveType() != null) {
            leaveRequest.setLeaveType(updateRequest.getLeaveType());
        }
        if (updateRequest.getStartDate() != null) {
            leaveRequest.setStartDate(updateRequest.getStartDate());
        }
        if (updateRequest.getEndDate() != null) {
            leaveRequest.setEndDate(updateRequest.getEndDate());
        }
        if (updateRequest.getReason() != null) {
            leaveRequest.setReason(updateRequest.getReason());
        }
        if (updateRequest.getStatus() != null) {
            leaveRequest.setStatus(updateRequest.getStatus());
        }
        leaveRequest.setUpdatedAt(LocalDateTime.now());

        LeaveRequest updated = leaveRepository.saveLeaveRequest(leaveRequest);
        return mapLeaveRequestToResponseDTO(updated);
    }

    public String deleteLeaveRequest(Long id) {
        if(!leaveRepository.existsLeaveRequestById(id)){
            return "LeaveRequest does not exist";
        }

        leaveRepository.deleteLeaveRequestById(id);
        return "LeaveRequest id: " + id + " has been deleted";
    }

    private LeaveResponseDTO mapLeaveRequestToResponseDTO(LeaveRequest leaveRequest) {
        if (leaveRequest == null) {
            return null;
        }
        return new LeaveResponseDTO(
                leaveRequest.getId(),
                leaveRequest.getEmployeeId(),
                leaveRequest.getLeaveType(),
                leaveRequest.getStartDate(),
                leaveRequest.getEndDate(),
                leaveRequest.getReason(),
                leaveRequest.getStatus()
        );
    }
}

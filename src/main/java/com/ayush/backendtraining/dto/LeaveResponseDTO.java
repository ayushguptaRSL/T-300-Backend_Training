package com.ayush.backendtraining.dto;

import com.ayush.backendtraining.model.LeaveStatus;
import com.ayush.backendtraining.model.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class LeaveResponseDTO {

    private Long id;
    private Long employeeId;
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private LeaveStatus status;

}

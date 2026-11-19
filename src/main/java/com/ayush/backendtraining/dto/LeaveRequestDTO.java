package com.ayush.backendtraining.dto;

import com.ayush.backendtraining.model.LeaveType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class LeaveRequestDTO {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Leave type is required")
    private LeaveType leaveType;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Can't apply leave for past date")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @FutureOrPresent(message = "Can't apply leave for past date")
    private LocalDate endDate;

    @NotBlank(message = "Reason is required")
    private String reason;
}

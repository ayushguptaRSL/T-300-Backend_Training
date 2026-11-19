package com.ayush.backendtraining.controller;

import com.ayush.backendtraining.dto.LeaveRequestDTO;
import com.ayush.backendtraining.dto.LeaveResponseDTO;
import com.ayush.backendtraining.dto.UpdateLeaveRequestDTO;
import com.ayush.backendtraining.exception.GlobalExceptionHandler;
import com.ayush.backendtraining.exception.InvalidDateRange;
import com.ayush.backendtraining.exception.ResourceNotFound;
import com.ayush.backendtraining.model.LeaveStatus;
import com.ayush.backendtraining.model.LeaveType;
import com.ayush.backendtraining.service.LeaveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LeaveControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LeaveService leaveService;

    @InjectMocks
    private LeaveController leaveController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(leaveController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /v1/api/leaves returns 200 OK and list of leaves")
    void getAllLeaves_ReturnsList() throws Exception {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(3);
        List<LeaveResponseDTO> leaves = List.of(
                new LeaveResponseDTO(1L, 101L, LeaveType.PAID_LEAVE, start, end, "Vacation", LeaveStatus.PENDING),
                new LeaveResponseDTO(2L, 102L, LeaveType.CAUSAL_LEAVE, start, end, "Doctor visit", LeaveStatus.APPROVED)
        );

        when(leaveService.getAllLeaveRequest()).thenReturn(leaves);

        mockMvc.perform(get("/v1/api/leaves")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].employeeId").value(101))
                .andExpect(jsonPath("$[0].leaveType").value("PAID_LEAVE"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].employeeId").value(102));

        verify(leaveService, times(1)).getAllLeaveRequest();
    }

    @Test
    @DisplayName("GET /v1/api/leaves/{id} returns 200 OK when found")
    void getLeaveById_Found_Returns200() throws Exception {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(3);
        LeaveResponseDTO response = new LeaveResponseDTO(
                1L, 101L, LeaveType.PAID_LEAVE, start, end, "Vacation", LeaveStatus.PENDING
        );

        when(leaveService.getLeaveRequest(1L)).thenReturn(response);

        mockMvc.perform(get("/v1/api/leaves/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.employeeId").value(101))
                .andExpect(jsonPath("$.leaveType").value("PAID_LEAVE"))
                .andExpect(jsonPath("$.reason").value("Vacation"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(leaveService, times(1)).getLeaveRequest(1L);
    }

    @Test
    @DisplayName("GET /v1/api/leaves/{id} returns 404 NOT FOUND with ErrorDTO when not found")
    void getLeaveById_NotFound_Returns404() throws Exception {
        when(leaveService.getLeaveRequest(999L))
                .thenThrow(new ResourceNotFound("Leave Request with id 999 not found."));

        mockMvc.perform(get("/v1/api/leaves/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Leave Request with id 999 not found."))
                .andExpect(jsonPath("$.path").value("/v1/api/leaves/999"));

        verify(leaveService, times(1)).getLeaveRequest(999L);
    }

    @Test
    @DisplayName("POST /v1/api/leaves returns 201 CREATED when valid")
    void createLeaveRequest_Valid_Returns201() throws Exception {
        LocalDate start = LocalDate.now().plusDays(2);
        LocalDate end = LocalDate.now().plusDays(5);
        String requestJson = String.format("""
                {
                    "employeeId": 101,
                    "leaveType": "PAID_LEAVE",
                    "startDate": "%s",
                    "endDate": "%s",
                    "reason": "Family trip"
                }
                """, start, end);

        LeaveResponseDTO responseDTO = new LeaveResponseDTO(
                1L, 101L, LeaveType.PAID_LEAVE, start, end, "Family trip", LeaveStatus.PENDING
        );

        when(leaveService.createLeaveRequest(any(LeaveRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/v1/api/leaves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.employeeId").value(101))
                .andExpect(jsonPath("$.leaveType").value("PAID_LEAVE"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(leaveService, times(1)).createLeaveRequest(any(LeaveRequestDTO.class));
    }

    @Test
    @DisplayName("POST /v1/api/leaves returns 422 UNPROCESSABLE_CONTENT when date range is invalid")
    void createLeaveRequest_InvalidDateRange_Returns422() throws Exception {
        LocalDate start = LocalDate.now().plusDays(5);
        LocalDate end = LocalDate.now().plusDays(2);
        String requestJson = String.format("""
                {
                    "employeeId": 101,
                    "leaveType": "PAID_LEAVE",
                    "startDate": "%s",
                    "endDate": "%s",
                    "reason": "Invalid dates"
                }
                """, start, end);

        when(leaveService.createLeaveRequest(any(LeaveRequestDTO.class)))
                .thenThrow(new InvalidDateRange("Start date must be before or equal to end date."));

        mockMvc.perform(post("/v1/api/leaves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.error").value("Unprocessable Content"))
                .andExpect(jsonPath("$.message").value("Start date must be before or equal to end date."))
                .andExpect(jsonPath("$.path").value("/v1/api/leaves"));

        verify(leaveService, times(1)).createLeaveRequest(any(LeaveRequestDTO.class));
    }

    @Test
    @DisplayName("POST /v1/api/leaves returns 400 BAD_REQUEST when validation fails")
    void createLeaveRequest_MissingRequiredFields_Returns400() throws Exception {
        // Missing employeeId, leaveType, reason
        String invalidJson = """
                {
                    "startDate": "2026-12-01",
                    "endDate": "2026-12-05"
                }
                """;

        mockMvc.perform(post("/v1/api/leaves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.fieldErrors").isMap())
                .andExpect(jsonPath("$.path").value("/v1/api/leaves"));

        verify(leaveService, never()).createLeaveRequest(any(LeaveRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /v1/api/leaves/{id} returns 200 OK when update is successful")
    void updateLeaveRequest_Valid_Returns200() throws Exception {
        LocalDate start = LocalDate.now().plusDays(3);
        LocalDate end = LocalDate.now().plusDays(6);
        String updateJson = String.format("""
                {
                    "leaveType": "CAUSAL_LEAVE",
                    "startDate": "%s",
                    "endDate": "%s",
                    "reason": "Updated reason",
                    "status": "APPROVED"
                }
                """, start, end);

        LeaveResponseDTO updatedResponse = new LeaveResponseDTO(
                1L, 101L, LeaveType.CAUSAL_LEAVE, start, end, "Updated reason", LeaveStatus.APPROVED
        );

        when(leaveService.updateLeaveRequest(eq(1L), any(UpdateLeaveRequestDTO.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/v1/api/leaves/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.leaveType").value("CAUSAL_LEAVE"))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(leaveService, times(1)).updateLeaveRequest(eq(1L), any(UpdateLeaveRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /v1/api/leaves/{id} returns 404 NOT FOUND when leave does not exist")
    void updateLeaveRequest_NotFound_Returns404() throws Exception {
        String updateJson = """
                {
                    "reason": "New Reason"
                }
                """;

        when(leaveService.updateLeaveRequest(eq(999L), any(UpdateLeaveRequestDTO.class)))
                .thenThrow(new ResourceNotFound("Leave Request with id 999 not found."));

        mockMvc.perform(put("/v1/api/leaves/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Leave Request with id 999 not found."))
                .andExpect(jsonPath("$.path").value("/v1/api/leaves/999"));

        verify(leaveService, times(1)).updateLeaveRequest(eq(999L), any(UpdateLeaveRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /v1/api/leaves/{id} returns 422 UNPROCESSABLE_CONTENT when date range is invalid")
    void updateLeaveRequest_InvalidDateRange_Returns422() throws Exception {
        LocalDate start = LocalDate.now().plusDays(10);
        LocalDate end = LocalDate.now().plusDays(2);
        String updateJson = String.format("""
                {
                    "startDate": "%s",
                    "endDate": "%s"
                }
                """, start, end);

        when(leaveService.updateLeaveRequest(eq(1L), any(UpdateLeaveRequestDTO.class)))
                .thenThrow(new InvalidDateRange("Start date must be before or equal to end date."));

        mockMvc.perform(put("/v1/api/leaves/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.error").value("Unprocessable Content"))
                .andExpect(jsonPath("$.message").value("Start date must be before or equal to end date."));

        verify(leaveService, times(1)).updateLeaveRequest(eq(1L), any(UpdateLeaveRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE /v1/api/leaves/{id} returns 200 OK with confirmation message")
    void deleteLeaveRequest_Found_Returns200() throws Exception {
        when(leaveService.deleteLeaveRequest(1L))
                .thenReturn("LeaveRequest id: 1 has been deleted");

        mockMvc.perform(delete("/v1/api/leaves/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("LeaveRequest id: 1 has been deleted"));

        verify(leaveService, times(1)).deleteLeaveRequest(1L);
    }

    @Test
    @DisplayName("DELETE /v1/api/leaves/{id} returns 404 NOT FOUND when not found")
    void deleteLeaveRequest_NotFound_Returns404() throws Exception {
        when(leaveService.deleteLeaveRequest(999L))
                .thenThrow(new ResourceNotFound("Leave Request with id 999 not found."));

        mockMvc.perform(delete("/v1/api/leaves/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Leave Request with id 999 not found."));

        verify(leaveService, times(1)).deleteLeaveRequest(999L);
    }
}

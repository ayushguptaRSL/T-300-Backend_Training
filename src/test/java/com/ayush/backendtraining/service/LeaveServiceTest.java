package com.ayush.backendtraining.service;

import com.ayush.backendtraining.dto.LeaveRequestDTO;
import com.ayush.backendtraining.dto.LeaveResponseDTO;
import com.ayush.backendtraining.dto.UpdateLeaveRequestDTO;
import com.ayush.backendtraining.exception.InvalidDateRange;
import com.ayush.backendtraining.exception.ResourceNotFound;
import com.ayush.backendtraining.model.LeaveStatus;
import com.ayush.backendtraining.model.LeaveType;
import com.ayush.backendtraining.repository.LeaveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LeaveServiceTest {

    private LeaveRepository leaveRepository;
    private LeaveService leaveService;

    @BeforeEach
    void setUp() {
        leaveRepository = new LeaveRepository();
        leaveService = new LeaveService(leaveRepository);
    }

    @Test
    @DisplayName("getAllLeaveRequest returns empty list when no leave requests exist")
    void getAllLeaveRequest_Empty_ReturnsEmptyList() {
        List<LeaveResponseDTO> result = leaveService.getAllLeaveRequest();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getAllLeaveRequest returns all saved leave requests")
    void getAllLeaveRequest_WithData_ReturnsAllLeaves() {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(3);

        leaveService.createLeaveRequest(new LeaveRequestDTO(1L, LeaveType.PAID_LEAVE, start, end, "Reason 1"));
        leaveService.createLeaveRequest(new LeaveRequestDTO(2L, LeaveType.CAUSAL_LEAVE, start, end, "Reason 2"));

        List<LeaveResponseDTO> result = leaveService.getAllLeaveRequest();
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("getLeaveRequest returns leave request when id exists")
    void getLeaveRequest_ExistingId_ReturnsLeave() {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(3);
        LeaveResponseDTO created = leaveService.createLeaveRequest(
                new LeaveRequestDTO(10L, LeaveType.PAID_LEAVE, start, end, "Vacation")
        );

        LeaveResponseDTO found = leaveService.getLeaveRequest(created.getId());
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals(10L, found.getEmployeeId());
        assertEquals(LeaveType.PAID_LEAVE, found.getLeaveType());
    }

    @Test
    @DisplayName("getLeaveRequest throws ResourceNotFound when id does not exist")
    void getLeaveRequest_NonExistingId_ThrowsResourceNotFound() {
        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () ->
                leaveService.getLeaveRequest(999L)
        );
        assertEquals("Leave Request with id 999 not found.", exception.getMessage());
    }

    @Test
    @DisplayName("createLeaveRequest creates leave successfully with valid dates")
    void createLeaveRequest_ValidDates_Success() {
        LocalDate start = LocalDate.now().plusDays(2);
        LocalDate end = LocalDate.now().plusDays(6);
        LeaveRequestDTO requestDTO = new LeaveRequestDTO(
                101L,
                LeaveType.PAID_LEAVE,
                start,
                end,
                "Family function"
        );

        LeaveResponseDTO created = leaveService.createLeaveRequest(requestDTO);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals(101L, created.getEmployeeId());
        assertEquals(LeaveType.PAID_LEAVE, created.getLeaveType());
        assertEquals(start, created.getStartDate());
        assertEquals(end, created.getEndDate());
        assertEquals(LeaveStatus.PENDING, created.getStatus());
    }

    @Test
    @DisplayName("createLeaveRequest succeeds when start date equals end date (single day leave)")
    void createLeaveRequest_SameStartAndEndDate_Success() {
        LocalDate sameDate = LocalDate.now().plusDays(3);
        LeaveRequestDTO requestDTO = new LeaveRequestDTO(
                101L,
                LeaveType.CAUSAL_LEAVE,
                sameDate,
                sameDate,
                "Dentist appointment"
        );

        LeaveResponseDTO created = leaveService.createLeaveRequest(requestDTO);
        assertNotNull(created);
        assertEquals(sameDate, created.getStartDate());
        assertEquals(sameDate, created.getEndDate());
    }

    @Test
    @DisplayName("createLeaveRequest throws InvalidDateRange when end date is before start date")
    void createLeaveRequest_EndDateBeforeStartDate_ThrowsInvalidDateRange() {
        LocalDate start = LocalDate.now().plusDays(5);
        LocalDate end = LocalDate.now().plusDays(2);
        LeaveRequestDTO requestDTO = new LeaveRequestDTO(
                101L,
                LeaveType.PAID_LEAVE,
                start,
                end,
                "Invalid dates"
        );

        InvalidDateRange exception = assertThrows(InvalidDateRange.class, () ->
                leaveService.createLeaveRequest(requestDTO)
        );
        assertEquals("Start date must be before or equal to end date.", exception.getMessage());
    }

    @Test
    @DisplayName("updateLeaveRequest successfully updates fields")
    void updateLeaveRequest_ValidUpdate_Success() {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(5);
        LeaveResponseDTO created = leaveService.createLeaveRequest(
                new LeaveRequestDTO(101L, LeaveType.PAID_LEAVE, start, end, "Old Reason")
        );

        LocalDate newStart = LocalDate.now().plusDays(2);
        LocalDate newEnd = LocalDate.now().plusDays(6);
        UpdateLeaveRequestDTO updateDTO = new UpdateLeaveRequestDTO(
                LeaveType.UNPAID_LEAVE,
                newStart,
                newEnd,
                "New Reason",
                LeaveStatus.APPROVED
        );

        LeaveResponseDTO updated = leaveService.updateLeaveRequest(created.getId(), updateDTO);

        assertEquals(LeaveType.UNPAID_LEAVE, updated.getLeaveType());
        assertEquals(newStart, updated.getStartDate());
        assertEquals(newEnd, updated.getEndDate());
        assertEquals("New Reason", updated.getReason());
        assertEquals(LeaveStatus.APPROVED, updated.getStatus());
    }

    @Test
    @DisplayName("updateLeaveRequest throws InvalidDateRange when updated dates have end before start")
    void updateLeaveRequest_BothDatesUpdated_EndDateBeforeStartDate_ThrowsInvalidDateRange() {
        LeaveResponseDTO created = leaveService.createLeaveRequest(
                new LeaveRequestDTO(101L, LeaveType.PAID_LEAVE, LocalDate.now().plusDays(5), LocalDate.now().plusDays(10), "Trip")
        );

        UpdateLeaveRequestDTO updateDTO = new UpdateLeaveRequestDTO();
        updateDTO.setStartDate(LocalDate.now().plusDays(8));
        updateDTO.setEndDate(LocalDate.now().plusDays(4));

        InvalidDateRange exception = assertThrows(InvalidDateRange.class, () ->
                leaveService.updateLeaveRequest(created.getId(), updateDTO)
        );
        assertEquals("Start date must be before or equal to end date.", exception.getMessage());
    }

    @Test
    @DisplayName("updateLeaveRequest throws InvalidDateRange when updated startDate is after existing endDate")
    void updateLeaveRequest_OnlyStartDateUpdated_AfterExistingEndDate_ThrowsInvalidDateRange() {
        LeaveResponseDTO created = leaveService.createLeaveRequest(
                new LeaveRequestDTO(101L, LeaveType.PAID_LEAVE, LocalDate.now().plusDays(2), LocalDate.now().plusDays(5), "Trip")
        );

        UpdateLeaveRequestDTO updateDTO = new UpdateLeaveRequestDTO();
        updateDTO.setStartDate(LocalDate.now().plusDays(7)); // after existing endDate (day +5)

        InvalidDateRange exception = assertThrows(InvalidDateRange.class, () ->
                leaveService.updateLeaveRequest(created.getId(), updateDTO)
        );
        assertEquals("Start date must be before or equal to end date.", exception.getMessage());
    }

    @Test
    @DisplayName("updateLeaveRequest throws InvalidDateRange when updated endDate is before existing startDate")
    void updateLeaveRequest_OnlyEndDateUpdated_BeforeExistingStartDate_ThrowsInvalidDateRange() {
        LeaveResponseDTO created = leaveService.createLeaveRequest(
                new LeaveRequestDTO(101L, LeaveType.PAID_LEAVE, LocalDate.now().plusDays(5), LocalDate.now().plusDays(10), "Trip")
        );

        UpdateLeaveRequestDTO updateDTO = new UpdateLeaveRequestDTO();
        updateDTO.setEndDate(LocalDate.now().plusDays(3)); // before existing startDate (day +5)

        InvalidDateRange exception = assertThrows(InvalidDateRange.class, () ->
                leaveService.updateLeaveRequest(created.getId(), updateDTO)
        );
        assertEquals("Start date must be before or equal to end date.", exception.getMessage());
    }

    @Test
    @DisplayName("updateLeaveRequest throws ResourceNotFound when id does not exist")
    void updateLeaveRequest_NonExistingId_ThrowsResourceNotFound() {
        UpdateLeaveRequestDTO updateDTO = new UpdateLeaveRequestDTO();
        updateDTO.setReason("Some Reason");

        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () ->
                leaveService.updateLeaveRequest(999L, updateDTO)
        );
        assertEquals("Leave Request with id 999 not found.", exception.getMessage());
    }

    @Test
    @DisplayName("deleteLeaveRequest deletes existing leave request")
    void deleteLeaveRequest_ExistingId_DeletesSuccessfully() {
        LeaveResponseDTO created = leaveService.createLeaveRequest(
                new LeaveRequestDTO(101L, LeaveType.PAID_LEAVE, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), "Trip")
        );

        String message = leaveService.deleteLeaveRequest(created.getId());
        assertEquals("LeaveRequest id: " + created.getId() + " has been deleted", message);

        assertThrows(ResourceNotFound.class, () -> leaveService.getLeaveRequest(created.getId()));
    }

    @Test
    @DisplayName("deleteLeaveRequest throws ResourceNotFound when id does not exist")
    void deleteLeaveRequest_NonExistingId_ThrowsResourceNotFound() {
        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () ->
                leaveService.deleteLeaveRequest(999L)
        );
        assertEquals("Leave Request with id 999 not found.", exception.getMessage());
    }
}

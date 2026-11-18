package com.ayush.backendtraining.repository;

import com.ayush.backendtraining.model.LeaveRequest;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class LeaveRepository {

    Map<Long, LeaveRequest> leaveRequests = new HashMap<>();
    private final AtomicLong counter = new AtomicLong(1);


    public List<LeaveRequest> findAllLeaveRequests() {
        return new ArrayList<>(leaveRequests.values());
    }

    public Optional<LeaveRequest> findLeaveRequestById(Long id) {

        return Optional.ofNullable(leaveRequests.get(id));
    }

    public LeaveRequest saveLeaveRequest(LeaveRequest leaveRequest) {
        if (leaveRequest.getId() == null) {
            leaveRequest.setId(counter.getAndIncrement());
        }

        leaveRequests.put(leaveRequest.getId(), leaveRequest);
        return leaveRequest;
    }

    public Boolean existsLeaveRequestById(Long id) {
        return leaveRequests.containsKey(id);
    }

    public void deleteLeaveRequestById(Long id) {
        leaveRequests.remove(id);
    }
}

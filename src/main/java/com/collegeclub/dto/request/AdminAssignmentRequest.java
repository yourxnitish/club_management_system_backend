package com.collegeclub.dto.request;

import jakarta.validation.constraints.NotNull;

public class AdminAssignmentRequest {

    @NotNull(message = "Club ID is required")
    private Long clubId;

    @NotNull(message = "User ID is required")
    private Long userId;

    public AdminAssignmentRequest() {
    }

    public AdminAssignmentRequest(Long clubId, Long userId) {
        this.clubId = clubId;
        this.userId = userId;
    }

    public Long getClubId() {
        return clubId;
    }

    public void setClubId(Long clubId) {
        this.clubId = clubId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

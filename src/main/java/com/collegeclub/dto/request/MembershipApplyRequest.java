package com.collegeclub.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class MembershipApplyRequest {

    @NotNull(message = "Club ID is required")
    private Long clubId;

    @Size(max = 1000, message = "Reason cannot exceed 1000 characters")
    private String joinReason;

    public MembershipApplyRequest() {
    }

    public Long getClubId() {
        return clubId;
    }

    public void setClubId(Long clubId) {
        this.clubId = clubId;
    }

    public String getJoinReason() {
        return joinReason;
    }

    public void setJoinReason(String joinReason) {
        this.joinReason = joinReason;
    }
}

package com.collegeclub.dto.request;

import com.collegeclub.enums.MembershipStatus;
import jakarta.validation.constraints.NotNull;

public class MembershipReviewRequest {

    @NotNull(message = "Status is required (APPROVED or REJECTED)")
    private MembershipStatus status;

    public MembershipReviewRequest() {
    }

    public MembershipReviewRequest(MembershipStatus status) {
        this.status = status;
    }

    public MembershipStatus getStatus() {
        return status;
    }

    public void setStatus(MembershipStatus status) {
        this.status = status;
    }
}

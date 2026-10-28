package com.collegeclub.controller;

import com.collegeclub.dto.request.MembershipApplyRequest;
import com.collegeclub.dto.request.MembershipReviewRequest;
import com.collegeclub.dto.response.ApiResponse;
import com.collegeclub.dto.response.MembershipResponse;
import com.collegeclub.entity.User;
import com.collegeclub.enums.MembershipStatus;
import com.collegeclub.security.UserPrincipal;
import com.collegeclub.service.MembershipService;
import com.collegeclub.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/memberships")
@Tag(name = "Memberships", description = "Endpoints for club membership applications, status tracking, and member approvals")
public class MembershipController {

    private final MembershipService membershipService;
    private final UserService userService;

    public MembershipController(MembershipService membershipService, UserService userService) {
        this.membershipService = membershipService;
        this.userService = userService;
    }

    @PostMapping("/apply")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Submit a club membership application (Student)")
    public ResponseEntity<ApiResponse<MembershipResponse>> apply(
            @Valid @RequestBody MembershipApplyRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        User student = userService.getUserEntity(principal.getId());
        MembershipResponse response = membershipService.applyForMembership(request, student);
        return ResponseEntity.ok(ApiResponse.ok("Membership request submitted successfully", response));
    }

    @GetMapping("/my-memberships")
    @Operation(summary = "Get personal club memberships and application statuses (Student)")
    public ResponseEntity<ApiResponse<List<MembershipResponse>>> getMyMemberships(
            @RequestParam(required = false) MembershipStatus status,
            @AuthenticationPrincipal UserPrincipal principal) {
        List<MembershipResponse> list = membershipService.getStudentMemberships(principal.getId(), status);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel a membership application (Student)")
    public ResponseEntity<ApiResponse<Void>> cancelMembership(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        User student = userService.getUserEntity(principal.getId());
        membershipService.cancelMembership(id, student);
        return ResponseEntity.ok(ApiResponse.success("Membership application cancelled"));
    }

    @GetMapping("/club/{clubId}")
    @PreAuthorize("hasAnyRole('CLUB_ADMIN', 'SYSTEM_ADMIN')")
    @Operation(summary = "Get membership list for a club (Club Admin or System Admin)")
    public ResponseEntity<ApiResponse<Page<MembershipResponse>>> getClubMemberships(
            @PathVariable Long clubId,
            @RequestParam(required = false) MembershipStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @AuthenticationPrincipal UserPrincipal principal) {

        User reviewer = userService.getUserEntity(principal.getId());
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<MembershipResponse> responses = membershipService.getClubMemberships(clubId, status, pageable, reviewer);
        return ResponseEntity.ok(ApiResponse.ok(responses));
    }

    @PatchMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('CLUB_ADMIN', 'SYSTEM_ADMIN')")
    @Operation(summary = "Approve or reject a club membership request (Club Admin or System Admin)")
    public ResponseEntity<ApiResponse<MembershipResponse>> reviewMembership(
            @PathVariable Long id,
            @Valid @RequestBody MembershipReviewRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        User reviewer = userService.getUserEntity(principal.getId());
        MembershipResponse response = membershipService.reviewMembership(id, request, reviewer);
        return ResponseEntity.ok(ApiResponse.ok("Membership reviewed successfully", response));
    }
}

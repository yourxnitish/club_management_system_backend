package com.collegeclub.controller;

import com.collegeclub.dto.request.FeedbackCreateRequest;
import com.collegeclub.dto.response.ApiResponse;
import com.collegeclub.dto.response.FeedbackResponse;
import com.collegeclub.entity.User;
import com.collegeclub.security.UserPrincipal;
import com.collegeclub.service.FeedbackService;
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
@RequestMapping("/api/v1/feedbacks")
@Tag(name = "Feedback", description = "Endpoints for submitting and viewing event ratings and attendee reviews")
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final UserService userService;

    public FeedbackController(FeedbackService feedbackService, UserService userService) {
        this.feedbackService = feedbackService;
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Submit feedback and star rating for an attended event (Student)")
    public ResponseEntity<ApiResponse<FeedbackResponse>> submitFeedback(
            @Valid @RequestBody FeedbackCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        User student = userService.getUserEntity(principal.getId());
        FeedbackResponse response = feedbackService.submitFeedback(request, student);
        return ResponseEntity.ok(ApiResponse.ok("Thank you! Feedback submitted successfully", response));
    }

    @GetMapping("/event/{eventId}")
    @Operation(summary = "Get attendee feedback for a specific event (Public)")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getFeedbackForEvent(@PathVariable Long eventId) {
        List<FeedbackResponse> list = feedbackService.getFeedbackForEvent(eventId);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/club/{clubId}")
    @PreAuthorize("hasAnyRole('CLUB_ADMIN', 'SYSTEM_ADMIN')")
    @Operation(summary = "Get feedback reviews for all events of a club (Club Admin or System Admin)")
    public ResponseEntity<ApiResponse<Page<FeedbackResponse>>> getFeedbackForClub(
            @PathVariable Long clubId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userService.getUserEntity(principal.getId());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<FeedbackResponse> responses = feedbackService.getFeedbackForClub(clubId, pageable, user);
        return ResponseEntity.ok(ApiResponse.ok(responses));
    }
}

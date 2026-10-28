package com.collegeclub.controller;

import com.collegeclub.dto.request.EventCreateRequest;
import com.collegeclub.dto.request.EventUpdateRequest;
import com.collegeclub.dto.response.ApiResponse;
import com.collegeclub.dto.response.EventResponse;
import com.collegeclub.entity.User;
import com.collegeclub.enums.EventStatus;
import com.collegeclub.security.UserPrincipal;
import com.collegeclub.service.EventService;
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
@RequestMapping("/api/v1/events")
@Tag(name = "Events", description = "Endpoints for discovering campus events, scheduling, and managing event lifecycles")
public class EventController {

    private final EventService eventService;
    private final UserService userService;

    public EventController(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Search and list events (Public)")
    public ResponseEntity<ApiResponse<Page<EventResponse>>> searchEvents(
            @RequestParam(required = false) Long clubId,
            @RequestParam(required = false) EventStatus status,
            @RequestParam(required = false) Boolean upcoming,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "startTime") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @AuthenticationPrincipal UserPrincipal principal) {

        User currentUser = principal != null ? userService.getUserEntity(principal.getId()) : null;
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<EventResponse> events = eventService.searchEvents(clubId, status, upcoming, search, pageable, currentUser);
        return ResponseEntity.ok(ApiResponse.ok(events));
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get featured upcoming events (Public)")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getUpcoming(
            @RequestParam(defaultValue = "6") int limit,
            @AuthenticationPrincipal UserPrincipal principal) {
        User currentUser = principal != null ? userService.getUserEntity(principal.getId()) : null;
        return ResponseEntity.ok(ApiResponse.ok(eventService.getUpcomingEvents(limit, currentUser)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event details by ID (Public)")
    public ResponseEntity<ApiResponse<EventResponse>> getEventById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        User currentUser = principal != null ? userService.getUserEntity(principal.getId()) : null;
        return ResponseEntity.ok(ApiResponse.ok(eventService.getEventById(id, currentUser)));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get event details by Slug (Public)")
    public ResponseEntity<ApiResponse<EventResponse>> getEventBySlug(
            @PathVariable String slug,
            @AuthenticationPrincipal UserPrincipal principal) {
        User currentUser = principal != null ? userService.getUserEntity(principal.getId()) : null;
        return ResponseEntity.ok(ApiResponse.ok(eventService.getEventBySlug(slug, currentUser)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CLUB_ADMIN', 'SYSTEM_ADMIN')")
    @Operation(summary = "Create a new club event (Club Admin or System Admin)")
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(
            @Valid @RequestBody EventCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        User creator = userService.getUserEntity(principal.getId());
        EventResponse response = eventService.createEvent(request, creator);
        return ResponseEntity.ok(ApiResponse.ok("Event created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLUB_ADMIN', 'SYSTEM_ADMIN')")
    @Operation(summary = "Update an existing event (Club Admin or System Admin)")
    public ResponseEntity<ApiResponse<EventResponse>> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userService.getUserEntity(principal.getId());
        EventResponse response = eventService.updateEvent(id, request, user);
        return ResponseEntity.ok(ApiResponse.ok("Event updated successfully", response));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('CLUB_ADMIN', 'SYSTEM_ADMIN')")
    @Operation(summary = "Cancel an event (Club Admin or System Admin)")
    public ResponseEntity<ApiResponse<EventResponse>> cancelEvent(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userService.getUserEntity(principal.getId());
        EventResponse response = eventService.cancelEvent(id, user);
        return ResponseEntity.ok(ApiResponse.ok("Event cancelled", response));
    }
}

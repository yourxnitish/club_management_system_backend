package com.collegeclub.controller;

import com.collegeclub.dto.request.AdminAssignmentRequest;
import com.collegeclub.dto.request.ClubCreateRequest;
import com.collegeclub.dto.request.ClubUpdateRequest;
import com.collegeclub.dto.response.ApiResponse;
import com.collegeclub.dto.response.ClubResponse;
import com.collegeclub.entity.User;
import com.collegeclub.enums.ClubStatus;
import com.collegeclub.security.UserPrincipal;
import com.collegeclub.service.ClubService;
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
@RequestMapping("/api/v1/clubs")
@Tag(name = "Clubs", description = "Endpoints for club discovery, club profile editing, and club administration")
public class ClubController {

    private final ClubService clubService;
    private final UserService userService;

    public ClubController(ClubService clubService, UserService userService) {
        this.clubService = clubService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Search and list clubs (Public)")
    public ResponseEntity<ApiResponse<Page<ClubResponse>>> searchClubs(
            @RequestParam(required = false) ClubStatus status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ClubResponse> clubs = clubService.searchClubs(status, category, search, pageable);
        return ResponseEntity.ok(ApiResponse.ok(clubs));
    }

    @GetMapping("/categories")
    @Operation(summary = "Get list of all distinct active club categories")
    public ResponseEntity<ApiResponse<List<String>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.ok(clubService.getCategories()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get club details by ID (Public)")
    public ResponseEntity<ApiResponse<ClubResponse>> getClubById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(clubService.getClubById(id)));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get club details by Slug (Public)")
    public ResponseEntity<ApiResponse<ClubResponse>> getClubBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.ok(clubService.getClubBySlug(slug)));
    }

    @GetMapping("/my-managed")
    @PreAuthorize("hasAnyRole('CLUB_ADMIN', 'SYSTEM_ADMIN')")
    @Operation(summary = "Get clubs managed by current club administrator")
    public ResponseEntity<ApiResponse<List<ClubResponse>>> getMyManagedClubs(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok(clubService.getClubsForUser(principal.getId())));
    }

    @PostMapping
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @Operation(summary = "Create a new club (System Admin only)")
    public ResponseEntity<ApiResponse<ClubResponse>> createClub(
            @Valid @RequestBody ClubCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        User creator = userService.getUserEntity(principal.getId());
        ClubResponse response = clubService.createClub(request, creator);
        return ResponseEntity.ok(ApiResponse.ok("Club created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLUB_ADMIN', 'SYSTEM_ADMIN')")
    @Operation(summary = "Update club details (Assigned Club Admin or System Admin)")
    public ResponseEntity<ApiResponse<ClubResponse>> updateClub(
            @PathVariable Long id,
            @Valid @RequestBody ClubUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userService.getUserEntity(principal.getId());
        ClubResponse response = clubService.updateClub(id, request, user);
        return ResponseEntity.ok(ApiResponse.ok("Club updated successfully", response));
    }

    @PostMapping("/assign-admin")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @Operation(summary = "Assign a user as club administrator (System Admin only)")
    public ResponseEntity<ApiResponse<ClubResponse>> assignClubAdmin(
            @Valid @RequestBody AdminAssignmentRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        User systemAdmin = userService.getUserEntity(principal.getId());
        ClubResponse response = clubService.assignClubAdmin(request, systemAdmin);
        return ResponseEntity.ok(ApiResponse.ok("Club administrator assigned successfully", response));
    }

    @DeleteMapping("/{id}/admins/{userId}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @Operation(summary = "Remove a user from club administrator assignment (System Admin only)")
    public ResponseEntity<ApiResponse<ClubResponse>> removeClubAdmin(
            @PathVariable Long id,
            @PathVariable Long userId,
            @AuthenticationPrincipal UserPrincipal principal) {
        User systemAdmin = userService.getUserEntity(principal.getId());
        ClubResponse response = clubService.removeClubAdmin(id, userId, systemAdmin);
        return ResponseEntity.ok(ApiResponse.ok("Club administrator assignment removed", response));
    }
}

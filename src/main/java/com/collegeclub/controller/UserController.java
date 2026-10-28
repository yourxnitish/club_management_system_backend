package com.collegeclub.controller;

import com.collegeclub.dto.request.UserUpdateRequest;
import com.collegeclub.dto.response.ApiResponse;
import com.collegeclub.dto.response.UserResponse;
import com.collegeclub.entity.User;
import com.collegeclub.enums.Role;
import com.collegeclub.security.UserPrincipal;
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

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User profile management and system admin account operations")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getUserById(principal.getId())));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@AuthenticationPrincipal UserPrincipal principal,
                                                                   @Valid @RequestBody UserUpdateRequest request) {
        UserResponse response = userService.updateUserProfile(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @Operation(summary = "Search and list all users (System Admin only)")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> searchUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<UserResponse> users = userService.searchUsers(role, active, search, pageable);
        return ResponseEntity.ok(ApiResponse.ok(users));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @Operation(summary = "Activate or deactivate a user account (System Admin only)")
    public ResponseEntity<ApiResponse<UserResponse>> toggleUserActive(
            @PathVariable Long id,
            @RequestParam boolean active,
            @AuthenticationPrincipal UserPrincipal principal) {
        User adminUser = userService.getUserEntity(principal.getId());
        UserResponse response = userService.toggleUserActive(id, active, adminUser);
        return ResponseEntity.ok(ApiResponse.ok("User status updated successfully", response));
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @Operation(summary = "Change a user's role (System Admin only)")
    public ResponseEntity<ApiResponse<UserResponse>> changeUserRole(
            @PathVariable Long id,
            @RequestParam Role role,
            @AuthenticationPrincipal UserPrincipal principal) {
        User adminUser = userService.getUserEntity(principal.getId());
        UserResponse response = userService.changeUserRole(id, role, adminUser);
        return ResponseEntity.ok(ApiResponse.ok("User role updated successfully", response));
    }
}

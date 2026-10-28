package com.collegeclub.controller;

import com.collegeclub.dto.response.ActivityLogResponse;
import com.collegeclub.dto.response.ApiResponse;
import com.collegeclub.dto.response.DashboardStatsResponse;
import com.collegeclub.entity.User;
import com.collegeclub.enums.Role;
import com.collegeclub.security.UserPrincipal;
import com.collegeclub.service.ActivityLogService;
import com.collegeclub.service.DashboardService;
import com.collegeclub.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard", description = "Endpoints for analytical dashboard statistics and platform audit logging")
public class DashboardController {

    private final DashboardService dashboardService;
    private final ActivityLogService activityLogService;
    private final UserService userService;

    public DashboardController(DashboardService dashboardService,
                               ActivityLogService activityLogService,
                               UserService userService) {
        this.dashboardService = dashboardService;
        this.activityLogService = activityLogService;
        this.userService = userService;
    }

    @GetMapping("/public-stats")
    @Operation(summary = "Get platform overview metrics for public landing page (Public)")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getPublicStats() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getPublicStats()));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get role-tailored dashboard metrics for current authenticated user")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getStats(@AuthenticationPrincipal UserPrincipal principal) {
        User user = userService.getUserEntity(principal.getId());
        DashboardStatsResponse stats;
        if (user.getRole() == Role.SYSTEM_ADMIN) {
            stats = dashboardService.getSystemAdminStats();
        } else if (user.getRole() == Role.CLUB_ADMIN) {
            stats = dashboardService.getClubAdminStats(user);
        } else {
            stats = dashboardService.getStudentStats(user);
        }
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping("/logs")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @Operation(summary = "View platform activity audit logs (System Admin only)")
    public ResponseEntity<ApiResponse<Page<ActivityLogResponse>>> getLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.ok(activityLogService.getAllLogs(pageable)));
    }
}

package com.collegeclub.controller;

import com.collegeclub.dto.request.AttendanceUpdateRequest;
import com.collegeclub.dto.response.ApiResponse;
import com.collegeclub.dto.response.RegistrationResponse;
import com.collegeclub.entity.User;
import com.collegeclub.enums.RegistrationStatus;
import com.collegeclub.security.UserPrincipal;
import com.collegeclub.service.RegistrationService;
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
@RequestMapping("/api/v1/registrations")
@Tag(name = "Registrations & Attendance", description = "Endpoints for event RSVP, seat reservations, and attendance tracking")
public class RegistrationController {

    private final RegistrationService registrationService;
    private final UserService userService;

    public RegistrationController(RegistrationService registrationService, UserService userService) {
        this.registrationService = registrationService;
        this.userService = userService;
    }

    @PostMapping("/event/{eventId}")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Register for an event (Student)")
    public ResponseEntity<ApiResponse<RegistrationResponse>> registerForEvent(
            @PathVariable Long eventId,
            @AuthenticationPrincipal UserPrincipal principal) {
        User student = userService.getUserEntity(principal.getId());
        RegistrationResponse response = registrationService.registerForEvent(eventId, student);
        return ResponseEntity.ok(ApiResponse.ok("Successfully registered for event", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel an event registration (Student)")
    public ResponseEntity<ApiResponse<Void>> cancelRegistration(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        User student = userService.getUserEntity(principal.getId());
        registrationService.cancelRegistration(id, student);
        return ResponseEntity.ok(ApiResponse.success("Registration cancelled successfully"));
    }

    @GetMapping("/my-registrations")
    @Operation(summary = "Get current student's event registrations and attendance history")
    public ResponseEntity<ApiResponse<List<RegistrationResponse>>> getMyRegistrations(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<RegistrationResponse> list = registrationService.getStudentRegistrations(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/event/{eventId}")
    @PreAuthorize("hasAnyRole('CLUB_ADMIN', 'SYSTEM_ADMIN')")
    @Operation(summary = "Get registration roster for an event (Club Admin or System Admin)")
    public ResponseEntity<ApiResponse<Page<RegistrationResponse>>> getEventRegistrations(
            @PathVariable Long eventId,
            @RequestParam(required = false) RegistrationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @AuthenticationPrincipal UserPrincipal principal) {

        User adminUser = userService.getUserEntity(principal.getId());
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<RegistrationResponse> list = registrationService.getEventRegistrations(eventId, status, pageable, adminUser);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @PatchMapping("/attendance")
    @PreAuthorize("hasAnyRole('CLUB_ADMIN', 'SYSTEM_ADMIN')")
    @Operation(summary = "Mark or update attendance for a registered participant (Club Admin or System Admin)")
    public ResponseEntity<ApiResponse<RegistrationResponse>> updateAttendance(
            @Valid @RequestBody AttendanceUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        User adminUser = userService.getUserEntity(principal.getId());
        RegistrationResponse response = registrationService.updateAttendance(request, adminUser);
        return ResponseEntity.ok(ApiResponse.ok("Attendance recorded", response));
    }
}

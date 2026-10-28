package com.collegeclub.controller;

import com.collegeclub.dto.request.AnnouncementCreateRequest;
import com.collegeclub.dto.response.AnnouncementResponse;
import com.collegeclub.dto.response.ApiResponse;
import com.collegeclub.entity.User;
import com.collegeclub.security.UserPrincipal;
import com.collegeclub.service.AnnouncementService;
import com.collegeclub.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/announcements")
@Tag(name = "Announcements", description = "Endpoints for publishing and consuming campus news and club updates")
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final UserService userService;

    public AnnouncementController(AnnouncementService announcementService, UserService userService) {
        this.announcementService = announcementService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get announcement feed (Public announcements or authenticated student personalized feed)")
    public ResponseEntity<ApiResponse<List<AnnouncementResponse>>> getFeed(
            @RequestParam(defaultValue = "10") int limit,
            @AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.ok(ApiResponse.ok(announcementService.getPublicAnnouncements(limit)));
        }
        User user = userService.getUserEntity(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(announcementService.getAnnouncementsForUser(user, limit)));
    }

    @GetMapping("/public")
    @Operation(summary = "Get campus-wide public announcements")
    public ResponseEntity<ApiResponse<List<AnnouncementResponse>>> getPublicAnnouncements(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(announcementService.getPublicAnnouncements(limit)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CLUB_ADMIN', 'SYSTEM_ADMIN')")
    @Operation(summary = "Publish an announcement (Club Admin or System Admin)")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> createAnnouncement(
            @Valid @RequestBody AnnouncementCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        User author = userService.getUserEntity(principal.getId());
        AnnouncementResponse response = announcementService.createAnnouncement(request, author);
        return ResponseEntity.ok(ApiResponse.ok("Announcement published successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLUB_ADMIN', 'SYSTEM_ADMIN')")
    @Operation(summary = "Delete an announcement (Club Admin or System Admin)")
    public ResponseEntity<ApiResponse<Void>> deleteAnnouncement(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userService.getUserEntity(principal.getId());
        announcementService.deleteAnnouncement(id, user);
        return ResponseEntity.ok(ApiResponse.success("Announcement deleted successfully"));
    }
}

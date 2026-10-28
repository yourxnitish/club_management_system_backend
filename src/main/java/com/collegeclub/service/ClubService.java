package com.collegeclub.service;

import com.collegeclub.dto.request.AdminAssignmentRequest;
import com.collegeclub.dto.request.ClubCreateRequest;
import com.collegeclub.dto.request.ClubUpdateRequest;
import com.collegeclub.dto.response.ClubResponse;
import com.collegeclub.dto.response.UserResponse;
import com.collegeclub.entity.Club;
import com.collegeclub.entity.ClubAdminAssignment;
import com.collegeclub.entity.User;
import com.collegeclub.enums.ClubStatus;
import com.collegeclub.enums.MembershipStatus;
import com.collegeclub.enums.Role;
import com.collegeclub.exception.BadRequestException;
import com.collegeclub.exception.ForbiddenException;
import com.collegeclub.exception.ResourceNotFoundException;
import com.collegeclub.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ClubService {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    private final ClubRepository clubRepository;
    private final ClubAdminAssignmentRepository assignmentRepository;
    private final ClubMembershipRepository membershipRepository;
    private final EventRepository eventRepository;
    private final EventFeedbackRepository feedbackRepository;
    private final UserService userService;
    private final ActivityLogService activityLogService;

    public ClubService(ClubRepository clubRepository,
                       ClubAdminAssignmentRepository assignmentRepository,
                       ClubMembershipRepository membershipRepository,
                       EventRepository eventRepository,
                       EventFeedbackRepository feedbackRepository,
                       UserService userService,
                       ActivityLogService activityLogService) {
        this.clubRepository = clubRepository;
        this.assignmentRepository = assignmentRepository;
        this.membershipRepository = membershipRepository;
        this.eventRepository = eventRepository;
        this.feedbackRepository = feedbackRepository;
        this.userService = userService;
        this.activityLogService = activityLogService;
    }

    @Transactional(readOnly = true)
    public Club getClubEntity(Long id) {
        return clubRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Club", "id", id));
    }

    @Transactional(readOnly = true)
    public ClubResponse getClubById(Long id) {
        return mapToResponse(getClubEntity(id));
    }

    @Transactional(readOnly = true)
    public ClubResponse getClubBySlug(String slug) {
        Club club = clubRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Club", "slug", slug));
        return mapToResponse(club);
    }

    @Transactional(readOnly = true)
    public Page<ClubResponse> searchClubs(ClubStatus status, String category, String search, Pageable pageable) {
        return clubRepository.searchClubs(status, category, search, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public List<String> getCategories() {
        return clubRepository.findDistinctCategories();
    }

    @Transactional(readOnly = true)
    public List<ClubResponse> getClubsForUser(Long userId) {
        List<Long> clubIds = assignmentRepository.findClubIdsByUserId(userId);
        return clubRepository.findAllById(clubIds).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClubResponse createClub(ClubCreateRequest request, User creator) {
        if (clubRepository.existsByName(request.getName().trim())) {
            throw new BadRequestException("A club with name '" + request.getName() + "' already exists.");
        }

        String baseSlug = toSlug(request.getName());
        String slug = baseSlug;
        int count = 1;
        while (clubRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + count++;
        }

        Club club = new Club();
        club.setName(request.getName().trim());
        club.setSlug(slug);
        club.setShortDescription(request.getShortDescription().trim());
        club.setDescription(request.getDescription().trim());
        club.setCategory(request.getCategory().trim());
        club.setBannerUrl(request.getBannerUrl());
        club.setLogoUrl(request.getLogoUrl());
        club.setContactEmail(request.getContactEmail().trim());
        club.setMeetingSchedule(request.getMeetingSchedule());
        club.setLocation(request.getLocation());
        club.setStatus(ClubStatus.ACTIVE);
        club.setCreatedBy(creator);

        club = clubRepository.save(club);

        activityLogService.log(creator, "CLUB_CREATED", "CLUB", club.getId(),
                "Club created: " + club.getName(), null);

        return mapToResponse(club);
    }

    @Transactional
    public ClubResponse updateClub(Long clubId, ClubUpdateRequest request, User user) {
        Club club = getClubEntity(clubId);
        verifyClubAdminOrSystemAdmin(club, user);

        if (request.getName() != null && !request.getName().isBlank()) {
            if (!club.getName().equalsIgnoreCase(request.getName().trim()) &&
                    clubRepository.existsByName(request.getName().trim())) {
                throw new BadRequestException("Club name '" + request.getName() + "' is already in use.");
            }
            club.setName(request.getName().trim());
        }

        if (request.getShortDescription() != null) {
            club.setShortDescription(request.getShortDescription().trim());
        }
        if (request.getDescription() != null) {
            club.setDescription(request.getDescription().trim());
        }
        if (request.getCategory() != null) {
            club.setCategory(request.getCategory().trim());
        }
        if (request.getBannerUrl() != null) {
            club.setBannerUrl(request.getBannerUrl().trim());
        }
        if (request.getLogoUrl() != null) {
            club.setLogoUrl(request.getLogoUrl().trim());
        }
        if (request.getContactEmail() != null) {
            club.setContactEmail(request.getContactEmail().trim());
        }
        if (request.getMeetingSchedule() != null) {
            club.setMeetingSchedule(request.getMeetingSchedule().trim());
        }
        if (request.getLocation() != null) {
            club.setLocation(request.getLocation().trim());
        }
        if (request.getStatus() != null && user.getRole() == Role.SYSTEM_ADMIN) {
            club.setStatus(request.getStatus());
        }

        club = clubRepository.save(club);

        activityLogService.log(user, "CLUB_UPDATED", "CLUB", club.getId(),
                "Club updated: " + club.getName(), null);

        return mapToResponse(club);
    }

    @Transactional
    public ClubResponse assignClubAdmin(AdminAssignmentRequest request, User systemAdmin) {
        Club club = getClubEntity(request.getClubId());
        User targetUser = userService.getUserEntity(request.getUserId());

        if (assignmentRepository.existsByClubAndUser(club, targetUser)) {
            throw new BadRequestException("User " + targetUser.getEmail() + " is already an admin of " + club.getName());
        }

        // Elevate user role to CLUB_ADMIN if currently STUDENT
        if (targetUser.getRole() == Role.STUDENT) {
            targetUser.setRole(Role.CLUB_ADMIN);
        }

        ClubAdminAssignment assignment = new ClubAdminAssignment(club, targetUser, systemAdmin);
        assignmentRepository.save(assignment);

        activityLogService.log(systemAdmin, "CLUB_ADMIN_ASSIGNED", "CLUB_ASSIGNMENT", club.getId(),
                "Assigned " + targetUser.getEmail() + " as admin for club " + club.getName(), null);

        return mapToResponse(club);
    }

    @Transactional
    public ClubResponse removeClubAdmin(Long clubId, Long userId, User systemAdmin) {
        Club club = getClubEntity(clubId);
        User targetUser = userService.getUserEntity(userId);

        assignmentRepository.deleteByClubAndUser(club, targetUser);

        // If targetUser has no other club assignments and is not SYSTEM_ADMIN, recheck role
        if (targetUser.getRole() == Role.CLUB_ADMIN) {
            List<ClubAdminAssignment> otherAssignments = assignmentRepository.findByUser(targetUser);
            if (otherAssignments.isEmpty()) {
                targetUser.setRole(Role.STUDENT);
            }
        }

        activityLogService.log(systemAdmin, "CLUB_ADMIN_REMOVED", "CLUB_ASSIGNMENT", club.getId(),
                "Removed " + targetUser.getEmail() + " from club " + club.getName(), null);

        return mapToResponse(club);
    }

    public void verifyClubAdminOrSystemAdmin(Club club, User user) {
        if (user.getRole() == Role.SYSTEM_ADMIN) {
            return;
        }
        if (user.getRole() == Role.CLUB_ADMIN) {
            boolean isAssigned = assignmentRepository.existsByClubAndUser(club, user);
            if (isAssigned) {
                return;
            }
        }
        throw new ForbiddenException("You do not have administrative privileges for the club: " + club.getName());
    }

    public ClubResponse mapToResponse(Club club) {
        ClubResponse res = new ClubResponse();
        res.setId(club.getId());
        res.setName(club.getName());
        res.setSlug(club.getSlug());
        res.setShortDescription(club.getShortDescription());
        res.setDescription(club.getDescription());
        res.setCategory(club.getCategory());
        res.setBannerUrl(club.getBannerUrl());
        res.setLogoUrl(club.getLogoUrl());
        res.setContactEmail(club.getContactEmail());
        res.setMeetingSchedule(club.getMeetingSchedule());
        res.setLocation(club.getLocation());
        res.setStatus(club.getStatus());
        res.setCreatedAt(club.getCreatedAt());
        res.setUpdatedAt(club.getUpdatedAt());

        // Dynamic stats
        res.setMemberCount(membershipRepository.countByClubAndStatus(club, MembershipStatus.APPROVED));
        res.setEventCount(eventRepository.findByClub(club).size());

        Double avg = feedbackRepository.getAverageRatingForClub(club.getId());
        res.setAverageRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 5.0);

        List<User> admins = assignmentRepository.findAdminsByClubId(club.getId());
        res.setAdmins(admins.stream().map(userService::mapToResponse).collect(Collectors.toList()));

        return res;
    }

    private String toSlug(String input) {
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }
}

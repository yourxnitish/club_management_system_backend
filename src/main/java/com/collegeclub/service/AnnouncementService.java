package com.collegeclub.service;

import com.collegeclub.dto.request.AnnouncementCreateRequest;
import com.collegeclub.dto.response.AnnouncementResponse;
import com.collegeclub.entity.Announcement;
import com.collegeclub.entity.Club;
import com.collegeclub.entity.User;
import com.collegeclub.enums.MembershipStatus;
import com.collegeclub.enums.Priority;
import com.collegeclub.enums.Role;
import com.collegeclub.exception.ForbiddenException;
import com.collegeclub.exception.ResourceNotFoundException;
import com.collegeclub.repository.AnnouncementRepository;
import com.collegeclub.repository.ClubMembershipRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final ClubService clubService;
    private final ClubMembershipRepository membershipRepository;
    private final ActivityLogService activityLogService;

    public AnnouncementService(AnnouncementRepository announcementRepository,
                               ClubService clubService,
                               ClubMembershipRepository membershipRepository,
                               ActivityLogService activityLogService) {
        this.announcementRepository = announcementRepository;
        this.clubService = clubService;
        this.membershipRepository = membershipRepository;
        this.activityLogService = activityLogService;
    }

    @Transactional(readOnly = true)
    public List<AnnouncementResponse> getPublicAnnouncements(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return announcementRepository.findPublicAnnouncements(pageable).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AnnouncementResponse> getAnnouncementsForUser(User user, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        if (user.getRole() == Role.SYSTEM_ADMIN) {
            return announcementRepository.findAll(pageable).getContent().stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }

        List<Long> memberClubIds = membershipRepository.findByUser(user).stream()
                .filter(m -> m.getStatus() == MembershipStatus.APPROVED)
                .map(m -> m.getClub().getId())
                .collect(Collectors.toList());

        if (memberClubIds.isEmpty()) {
            return getPublicAnnouncements(limit);
        }

        return announcementRepository.findFeedForUserClubs(memberClubIds, pageable).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<AnnouncementResponse> searchAnnouncements(Long clubId, Priority priority, Boolean isPublic, Pageable pageable) {
        return announcementRepository.searchAnnouncements(clubId, priority, isPublic, pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public AnnouncementResponse createAnnouncement(AnnouncementCreateRequest request, User author) {
        Club club = null;
        if (request.getClubId() != null) {
            club = clubService.getClubEntity(request.getClubId());
            clubService.verifyClubAdminOrSystemAdmin(club, author);
        } else {
            // Only SYSTEM_ADMIN can create university-wide announcements
            if (author.getRole() != Role.SYSTEM_ADMIN) {
                throw new ForbiddenException("Only system administrators can publish campus-wide announcements.");
            }
        }

        Announcement announcement = new Announcement();
        announcement.setClub(club);
        announcement.setAuthor(author);
        announcement.setTitle(request.getTitle().trim());
        announcement.setContent(request.getContent().trim());
        announcement.setPriority(request.getPriority() != null ? request.getPriority() : Priority.NORMAL);
        announcement.setPublic(request.isPublic());

        announcement = announcementRepository.save(announcement);

        String scope = club != null ? "for " + club.getName() : "campus-wide";
        activityLogService.log(author, "ANNOUNCEMENT_CREATED", "ANNOUNCEMENT", announcement.getId(),
                "Published announcement: " + announcement.getTitle() + " (" + scope + ")", null);

        return mapToResponse(announcement);
    }

    @Transactional
    public void deleteAnnouncement(Long id, User user) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", "id", id));

        if (user.getRole() != Role.SYSTEM_ADMIN) {
            if (announcement.getClub() != null) {
                clubService.verifyClubAdminOrSystemAdmin(announcement.getClub(), user);
            } else {
                throw new ForbiddenException("You cannot delete campus-wide announcements.");
            }
        }

        announcementRepository.delete(announcement);
        activityLogService.log(user, "ANNOUNCEMENT_DELETED", "ANNOUNCEMENT", id,
                "Deleted announcement: " + announcement.getTitle(), null);
    }

    public AnnouncementResponse mapToResponse(Announcement a) {
        AnnouncementResponse res = new AnnouncementResponse();
        res.setId(a.getId());
        if (a.getClub() != null) {
            res.setClubId(a.getClub().getId());
            res.setClubName(a.getClub().getName());
        }
        res.setAuthorName(a.getAuthor().getFullName());
        res.setTitle(a.getTitle());
        res.setContent(a.getContent());
        res.setPriority(a.getPriority());
        res.setPublic(a.isPublic());
        res.setCreatedAt(a.getCreatedAt());
        return res;
    }
}

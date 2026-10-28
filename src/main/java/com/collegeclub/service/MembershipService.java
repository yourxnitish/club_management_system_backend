package com.collegeclub.service;

import com.collegeclub.dto.request.MembershipApplyRequest;
import com.collegeclub.dto.request.MembershipReviewRequest;
import com.collegeclub.dto.response.MembershipResponse;
import com.collegeclub.entity.Club;
import com.collegeclub.entity.ClubMembership;
import com.collegeclub.entity.User;
import com.collegeclub.enums.ClubStatus;
import com.collegeclub.enums.MembershipStatus;
import com.collegeclub.exception.BadRequestException;
import com.collegeclub.exception.ResourceNotFoundException;
import com.collegeclub.repository.ClubMembershipRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MembershipService {

    private final ClubMembershipRepository membershipRepository;
    private final ClubService clubService;
    private final ActivityLogService activityLogService;

    public MembershipService(ClubMembershipRepository membershipRepository,
                             ClubService clubService,
                             ActivityLogService activityLogService) {
        this.membershipRepository = membershipRepository;
        this.clubService = clubService;
        this.activityLogService = activityLogService;
    }

    @Transactional
    public MembershipResponse applyForMembership(MembershipApplyRequest request, User student) {
        Club club = clubService.getClubEntity(request.getClubId());

        if (club.getStatus() != ClubStatus.ACTIVE) {
            throw new BadRequestException("You cannot join an inactive club.");
        }

        Optional<ClubMembership> existing = membershipRepository.findByClubAndUser(club, student);
        if (existing.isPresent()) {
            ClubMembership m = existing.get();
            if (m.getStatus() == MembershipStatus.APPROVED) {
                throw new BadRequestException("You are already an active member of this club.");
            } else if (m.getStatus() == MembershipStatus.PENDING) {
                throw new BadRequestException("You already have a pending membership application for this club.");
            } else {
                // If previously rejected or cancelled, allow re-applying
                m.setStatus(MembershipStatus.PENDING);
                m.setJoinReason(request.getJoinReason());
                m.setReviewedBy(null);
                m.setReviewedAt(null);
                m = membershipRepository.save(m);
                activityLogService.log(student, "MEMBERSHIP_REAPPLIED", "CLUB_MEMBERSHIP", m.getId(),
                        "Re-applied to " + club.getName(), null);
                return mapToResponse(m);
            }
        }

        ClubMembership membership = new ClubMembership(club, student, request.getJoinReason());
        membership = membershipRepository.save(membership);

        activityLogService.log(student, "MEMBERSHIP_REQUESTED", "CLUB_MEMBERSHIP", membership.getId(),
                student.getFullName() + " applied to join " + club.getName(), null);

        return mapToResponse(membership);
    }

    @Transactional
    public MembershipResponse reviewMembership(Long membershipId, MembershipReviewRequest request, User reviewer) {
        ClubMembership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new ResourceNotFoundException("ClubMembership", "id", membershipId));

        clubService.verifyClubAdminOrSystemAdmin(membership.getClub(), reviewer);

        if (request.getStatus() != MembershipStatus.APPROVED && request.getStatus() != MembershipStatus.REJECTED) {
            throw new BadRequestException("Membership status must be APPROVED or REJECTED.");
        }

        membership.setStatus(request.getStatus());
        membership.setReviewedBy(reviewer);
        membership.setReviewedAt(LocalDateTime.now());
        membership = membershipRepository.save(membership);

        String action = request.getStatus() == MembershipStatus.APPROVED ? "MEMBERSHIP_APPROVED" : "MEMBERSHIP_REJECTED";
        activityLogService.log(reviewer, action, "CLUB_MEMBERSHIP", membership.getId(),
                "Reviewed membership for " + membership.getUser().getEmail() + " in " + membership.getClub().getName() + " -> " + request.getStatus(), null);

        return mapToResponse(membership);
    }

    @Transactional(readOnly = true)
    public Page<MembershipResponse> getClubMemberships(Long clubId, MembershipStatus status, Pageable pageable, User requester) {
        Club club = clubService.getClubEntity(clubId);
        clubService.verifyClubAdminOrSystemAdmin(club, requester);
        return membershipRepository.findByClubIdAndStatus(clubId, status, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public List<MembershipResponse> getStudentMemberships(Long userId, MembershipStatus status) {
        return membershipRepository.findByUserIdAndOptionalStatus(userId, status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelMembership(Long membershipId, User student) {
        ClubMembership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new ResourceNotFoundException("ClubMembership", "id", membershipId));

        if (!membership.getUser().getId().equals(student.getId())) {
            throw new BadRequestException("You can only cancel your own club membership applications.");
        }

        membership.setStatus(MembershipStatus.CANCELLED);
        membershipRepository.save(membership);

        activityLogService.log(student, "MEMBERSHIP_CANCELLED", "CLUB_MEMBERSHIP", membership.getId(),
                "Cancelled membership in " + membership.getClub().getName(), null);
    }

    public MembershipResponse mapToResponse(ClubMembership cm) {
        MembershipResponse res = new MembershipResponse();
        res.setId(cm.getId());
        res.setClubId(cm.getClub().getId());
        res.setClubName(cm.getClub().getName());
        res.setClubLogoUrl(cm.getClub().getLogoUrl());
        res.setUserId(cm.getUser().getId());
        res.setUserName(cm.getUser().getFullName());
        res.setUserEmail(cm.getUser().getEmail());
        res.setUserStudentId(cm.getUser().getStudentId());
        res.setUserDepartment(cm.getUser().getDepartment());
        res.setUserAvatarUrl(cm.getUser().getAvatarUrl());
        res.setStatus(cm.getStatus());
        res.setJoinReason(cm.getJoinReason());
        if (cm.getReviewedBy() != null) {
            res.setReviewedById(cm.getReviewedBy().getId());
            res.setReviewedByName(cm.getReviewedBy().getFullName());
        }
        res.setReviewedAt(cm.getReviewedAt());
        res.setCreatedAt(cm.getCreatedAt());
        return res;
    }
}

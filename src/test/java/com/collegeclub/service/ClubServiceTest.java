package com.collegeclub.service;

import com.collegeclub.dto.request.ClubCreateRequest;
import com.collegeclub.dto.response.ClubResponse;
import com.collegeclub.entity.Club;
import com.collegeclub.entity.User;
import com.collegeclub.enums.ClubStatus;
import com.collegeclub.enums.Role;
import com.collegeclub.exception.BadRequestException;
import com.collegeclub.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClubServiceTest {

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private ClubAdminAssignmentRepository assignmentRepository;

    @Mock
    private ClubMembershipRepository membershipRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventFeedbackRepository feedbackRepository;

    @Mock
    private UserService userService;

    @Mock
    private ActivityLogService activityLogService;

    @InjectMocks
    private ClubService clubService;

    private User adminUser;
    private Club sampleClub;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setEmail("admin@collegeclub.edu");
        adminUser.setRole(Role.SYSTEM_ADMIN);

        sampleClub = new Club();
        sampleClub.setId(100L);
        sampleClub.setName("Robotics Society");
        sampleClub.setSlug("robotics-society");
        sampleClub.setCategory("Technology");
        sampleClub.setShortDescription("Robotics club");
        sampleClub.setDescription("Comprehensive robotics description");
        sampleClub.setContactEmail("robotics@collegeclub.edu");
        sampleClub.setStatus(ClubStatus.ACTIVE);
    }

    @Test
    void createClub_ShouldThrowException_WhenClubNameExists() {
        ClubCreateRequest req = new ClubCreateRequest();
        req.setName("Robotics Society");

        when(clubRepository.existsByName("Robotics Society")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> clubService.createClub(req, adminUser));
        verify(clubRepository, never()).save(any(Club.class));
    }

    @Test
    void createClub_ShouldSaveClub_WhenPayloadIsValid() {
        ClubCreateRequest req = new ClubCreateRequest();
        req.setName("Robotics Society");
        req.setShortDescription("Short desc");
        req.setDescription("Full desc");
        req.setCategory("Technology");
        req.setContactEmail("robotics@collegeclub.edu");

        when(clubRepository.existsByName("Robotics Society")).thenReturn(false);
        when(clubRepository.existsBySlug(anyString())).thenReturn(false);
        when(clubRepository.save(any(Club.class))).thenReturn(sampleClub);
        when(membershipRepository.countByClubAndStatus(any(), any())).thenReturn(5L);
        when(eventRepository.findByClub(any())).thenReturn(Collections.emptyList());
        when(feedbackRepository.getAverageRatingForClub(anyLong())).thenReturn(4.8);

        ClubResponse response = clubService.createClub(req, adminUser);

        assertNotNull(response);
        assertEquals("Robotics Society", response.getName());
        assertEquals("robotics-society", response.getSlug());
    }

    @Test
    void getClubById_ShouldReturnClubResponse_WhenFound() {
        when(clubRepository.findById(100L)).thenReturn(Optional.of(sampleClub));
        when(membershipRepository.countByClubAndStatus(any(), any())).thenReturn(10L);
        when(eventRepository.findByClub(any())).thenReturn(Collections.emptyList());
        when(feedbackRepository.getAverageRatingForClub(100L)).thenReturn(5.0);

        ClubResponse response = clubService.getClubById(100L);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("Robotics Society", response.getName());
    }
}

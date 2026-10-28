package com.collegeclub.service;

import com.collegeclub.dto.request.EventCreateRequest;
import com.collegeclub.dto.response.EventResponse;
import com.collegeclub.entity.Club;
import com.collegeclub.entity.Event;
import com.collegeclub.entity.User;
import com.collegeclub.enums.EventStatus;
import com.collegeclub.enums.Role;
import com.collegeclub.exception.BadRequestException;
import com.collegeclub.repository.EventFeedbackRepository;
import com.collegeclub.repository.EventRegistrationRepository;
import com.collegeclub.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventRegistrationRepository registrationRepository;

    @Mock
    private EventFeedbackRepository feedbackRepository;

    @Mock
    private ClubService clubService;

    @Mock
    private ActivityLogService activityLogService;

    @InjectMocks
    private EventService eventService;

    private User adminUser;
    private Club sampleClub;
    private Event sampleEvent;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setRole(Role.SYSTEM_ADMIN);

        sampleClub = new Club();
        sampleClub.setId(10L);
        sampleClub.setName("Robotics Society");

        sampleEvent = new Event();
        sampleEvent.setId(50L);
        sampleEvent.setClub(sampleClub);
        sampleEvent.setTitle("Hardware Hackathon");
        sampleEvent.setSlug("hardware-hackathon");
        sampleEvent.setLocation("Lab 101");
        sampleEvent.setStartTime(LocalDateTime.now().plusDays(2));
        sampleEvent.setEndTime(LocalDateTime.now().plusDays(3));
        sampleEvent.setCapacity(50);
        sampleEvent.setStatus(EventStatus.PUBLISHED);
    }

    @Test
    void createEvent_ShouldThrowException_WhenEndTimeBeforeStartTime() {
        EventCreateRequest req = new EventCreateRequest();
        req.setClubId(10L);
        req.setTitle("Bad Event");
        req.setDescription("Desc");
        req.setLocation("Location");
        req.setStartTime(LocalDateTime.now().plusDays(5));
        req.setEndTime(LocalDateTime.now().plusDays(4)); // Earlier than start

        when(clubService.getClubEntity(10L)).thenReturn(sampleClub);

        assertThrows(BadRequestException.class, () -> eventService.createEvent(req, adminUser));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void createEvent_ShouldSucceed_WhenValidRequest() {
        EventCreateRequest req = new EventCreateRequest();
        req.setClubId(10L);
        req.setTitle("Hardware Hackathon");
        req.setDescription("Desc");
        req.setLocation("Lab 101");
        req.setStartTime(LocalDateTime.now().plusDays(2));
        req.setEndTime(LocalDateTime.now().plusDays(3));
        req.setCapacity(50);

        when(clubService.getClubEntity(10L)).thenReturn(sampleClub);
        when(eventRepository.findBySlug(anyString())).thenReturn(Optional.empty());
        when(eventRepository.save(any(Event.class))).thenReturn(sampleEvent);
        when(feedbackRepository.findByEvent(any())).thenReturn(Collections.emptyList());

        EventResponse response = eventService.createEvent(req, adminUser);

        assertNotNull(response);
        assertEquals("Hardware Hackathon", response.getTitle());
        assertEquals("Robotics Society", response.getClubName());
    }
}

package com.collegeclub.service;

import com.collegeclub.dto.request.EventCreateRequest;
import com.collegeclub.dto.request.EventUpdateRequest;
import com.collegeclub.dto.response.EventResponse;
import com.collegeclub.entity.Club;
import com.collegeclub.entity.Event;
import com.collegeclub.entity.User;
import com.collegeclub.enums.EventStatus;
import com.collegeclub.exception.BadRequestException;
import com.collegeclub.exception.ResourceNotFoundException;
import com.collegeclub.repository.EventFeedbackRepository;
import com.collegeclub.repository.EventRegistrationRepository;
import com.collegeclub.repository.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class EventService {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    private final EventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;
    private final EventFeedbackRepository feedbackRepository;
    private final ClubService clubService;
    private final ActivityLogService activityLogService;

    public EventService(EventRepository eventRepository,
                        EventRegistrationRepository registrationRepository,
                        EventFeedbackRepository feedbackRepository,
                        ClubService clubService,
                        ActivityLogService activityLogService) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.feedbackRepository = feedbackRepository;
        this.clubService = clubService;
        this.activityLogService = activityLogService;
    }

    @Transactional(readOnly = true)
    public Event getEventEntity(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));
    }

    @Transactional(readOnly = true)
    public EventResponse getEventById(Long id, User currentUser) {
        return mapToResponse(getEventEntity(id), currentUser);
    }

    @Transactional(readOnly = true)
    public EventResponse getEventBySlug(String slug, User currentUser) {
        Event event = eventRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "slug", slug));
        return mapToResponse(event, currentUser);
    }

    @Transactional(readOnly = true)
    public Page<EventResponse> searchEvents(Long clubId, EventStatus status, Boolean upcoming, String search, Pageable pageable, User currentUser) {
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.searchEvents(clubId, status, upcoming, now, search, pageable)
                .map(event -> mapToResponse(event, currentUser));
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getUpcomingEvents(int limit, User currentUser) {
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, limit);
        return eventRepository.findUpcomingPublishedEvents(now, pageable).stream()
                .map(event -> mapToResponse(event, currentUser))
                .collect(Collectors.toList());
    }

    @Transactional
    public EventResponse createEvent(EventCreateRequest request, User creator) {
        Club club = clubService.getClubEntity(request.getClubId());
        clubService.verifyClubAdminOrSystemAdmin(club, creator);

        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new BadRequestException("Event end time must be after start time.");
        }

        String baseSlug = toSlug(request.getTitle());
        String slug = baseSlug;
        int count = 1;
        while (eventRepository.findBySlug(slug).isPresent()) {
            slug = baseSlug + "-" + count++;
        }

        Event event = new Event();
        event.setClub(club);
        event.setTitle(request.getTitle().trim());
        event.setSlug(slug);
        event.setDescription(request.getDescription().trim());
        event.setLocation(request.getLocation().trim());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setRegistrationDeadline(request.getRegistrationDeadline() != null ?
                request.getRegistrationDeadline() : request.getStartTime());
        event.setCapacity(request.getCapacity() > 0 ? request.getCapacity() : 50);
        event.setRegisteredCount(0);
        event.setBannerUrl(request.getBannerUrl());
        event.setStatus(EventStatus.PUBLISHED);
        event.setCreatedBy(creator);

        event = eventRepository.save(event);

        activityLogService.log(creator, "EVENT_CREATED", "EVENT", event.getId(),
                "Event created: " + event.getTitle() + " for club " + club.getName(), null);

        return mapToResponse(event, creator);
    }

    @Transactional
    public EventResponse updateEvent(Long eventId, EventUpdateRequest request, User user) {
        Event event = getEventEntity(eventId);
        clubService.verifyClubAdminOrSystemAdmin(event.getClub(), user);

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            event.setTitle(request.getTitle().trim());
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription().trim());
        }
        if (request.getLocation() != null) {
            event.setLocation(request.getLocation().trim());
        }
        if (request.getStartTime() != null) {
            event.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            event.setEndTime(request.getEndTime());
        }
        if (event.getEndTime().isBefore(event.getStartTime())) {
            throw new BadRequestException("Event end time must be after start time.");
        }
        if (request.getRegistrationDeadline() != null) {
            event.setRegistrationDeadline(request.getRegistrationDeadline());
        }
        if (request.getCapacity() != null && request.getCapacity() > 0) {
            event.setCapacity(request.getCapacity());
        }
        if (request.getBannerUrl() != null) {
            event.setBannerUrl(request.getBannerUrl().trim());
        }
        if (request.getStatus() != null) {
            event.setStatus(request.getStatus());
        }

        event = eventRepository.save(event);

        activityLogService.log(user, "EVENT_UPDATED", "EVENT", event.getId(),
                "Event updated: " + event.getTitle(), null);

        return mapToResponse(event, user);
    }

    @Transactional
    public EventResponse cancelEvent(Long eventId, User user) {
        Event event = getEventEntity(eventId);
        clubService.verifyClubAdminOrSystemAdmin(event.getClub(), user);

        event.setStatus(EventStatus.CANCELLED);
        event = eventRepository.save(event);

        activityLogService.log(user, "EVENT_CANCELLED", "EVENT", event.getId(),
                "Event cancelled: " + event.getTitle(), null);

        return mapToResponse(event, user);
    }

    public EventResponse mapToResponse(Event event, User currentUser) {
        EventResponse res = new EventResponse();
        res.setId(event.getId());
        res.setClubId(event.getClub().getId());
        res.setClubName(event.getClub().getName());
        res.setTitle(event.getTitle());
        res.setSlug(event.getSlug());
        res.setDescription(event.getDescription());
        res.setLocation(event.getLocation());
        res.setStartTime(event.getStartTime());
        res.setEndTime(event.getEndTime());
        res.setRegistrationDeadline(event.getRegistrationDeadline());
        res.setCapacity(event.getCapacity());
        res.setRegisteredCount(event.getRegisteredCount());
        res.setBannerUrl(event.getBannerUrl());
        res.setStatus(event.getStatus());
        res.setCreatedAt(event.getCreatedAt());

        if (currentUser != null) {
            registrationRepository.findByEventAndUser(event, currentUser).ifPresent(reg -> {
                res.setUserRegistered(true);
                res.setUserAttended(reg.isAttended());
            });
        }

        Double avg = feedbackRepository.getAverageRatingForEvent(event.getId());
        res.setAverageRating(avg != null ? Math.round(avg * 10.0) / 10.0 : null);
        res.setFeedbackCount(feedbackRepository.findByEvent(event).size());

        return res;
    }

    private String toSlug(String input) {
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }
}

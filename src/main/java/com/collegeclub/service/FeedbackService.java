package com.collegeclub.service;

import com.collegeclub.dto.request.FeedbackCreateRequest;
import com.collegeclub.dto.response.FeedbackResponse;
import com.collegeclub.entity.Event;
import com.collegeclub.entity.EventFeedback;
import com.collegeclub.entity.EventRegistration;
import com.collegeclub.entity.User;
import com.collegeclub.enums.RegistrationStatus;
import com.collegeclub.exception.BadRequestException;
import com.collegeclub.repository.EventFeedbackRepository;
import com.collegeclub.repository.EventRegistrationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    private final EventFeedbackRepository feedbackRepository;
    private final EventRegistrationRepository registrationRepository;
    private final EventService eventService;
    private final ClubService clubService;
    private final ActivityLogService activityLogService;

    public FeedbackService(EventFeedbackRepository feedbackRepository,
                           EventRegistrationRepository registrationRepository,
                           EventService eventService,
                           ClubService clubService,
                           ActivityLogService activityLogService) {
        this.feedbackRepository = feedbackRepository;
        this.registrationRepository = registrationRepository;
        this.eventService = eventService;
        this.clubService = clubService;
        this.activityLogService = activityLogService;
    }

    @Transactional
    public FeedbackResponse submitFeedback(FeedbackCreateRequest request, User student) {
        Event event = eventService.getEventEntity(request.getEventId());

        // Check if student registered for this event
        Optional<EventRegistration> regOpt = registrationRepository.findByEventAndUser(event, student);
        if (regOpt.isEmpty() || regOpt.get().getStatus() != RegistrationStatus.REGISTERED) {
            throw new BadRequestException("You can only submit feedback for events you registered for.");
        }

        if (feedbackRepository.existsByEventAndUser(event, student)) {
            throw new BadRequestException("You have already submitted feedback for this event.");
        }

        EventFeedback feedback = new EventFeedback(
                event,
                student,
                request.getRating(),
                request.getComments() != null ? request.getComments().trim() : null
        );
        feedback = feedbackRepository.save(feedback);

        activityLogService.log(student, "FEEDBACK_SUBMITTED", "EVENT_FEEDBACK", feedback.getId(),
                "Feedback submitted (" + request.getRating() + "/5) for " + event.getTitle(), null);

        return mapToResponse(feedback);
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> getFeedbackForEvent(Long eventId) {
        Event event = eventService.getEventEntity(eventId);
        return feedbackRepository.findByEvent(event).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<FeedbackResponse> getFeedbackForClub(Long clubId, Pageable pageable, User adminUser) {
        clubService.verifyClubAdminOrSystemAdmin(clubService.getClubEntity(clubId), adminUser);
        return feedbackRepository.findByClubId(clubId, pageable)
                .map(this::mapToResponse);
    }

    public FeedbackResponse mapToResponse(EventFeedback f) {
        FeedbackResponse res = new FeedbackResponse();
        res.setId(f.getId());
        res.setEventId(f.getEvent().getId());
        res.setEventTitle(f.getEvent().getTitle());
        res.setClubId(f.getEvent().getClub().getId());
        res.setClubName(f.getEvent().getClub().getName());
        res.setUserId(f.getUser().getId());
        res.setUserName(f.getUser().getFullName());
        res.setUserAvatarUrl(f.getUser().getAvatarUrl());
        res.setRating(f.getRating());
        res.setComments(f.getComments());
        res.setCreatedAt(f.getCreatedAt());
        return res;
    }
}

package com.collegeclub.service;

import com.collegeclub.dto.request.AttendanceUpdateRequest;
import com.collegeclub.dto.response.RegistrationResponse;
import com.collegeclub.entity.Event;
import com.collegeclub.entity.EventRegistration;
import com.collegeclub.entity.User;
import com.collegeclub.enums.EventStatus;
import com.collegeclub.enums.RegistrationStatus;
import com.collegeclub.exception.BadRequestException;
import com.collegeclub.exception.ResourceNotFoundException;
import com.collegeclub.repository.EventRegistrationRepository;
import com.collegeclub.repository.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RegistrationService {

    private final EventRegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final EventService eventService;
    private final ClubService clubService;
    private final ActivityLogService activityLogService;

    public RegistrationService(EventRegistrationRepository registrationRepository,
                               EventRepository eventRepository,
                               EventService eventService,
                               ClubService clubService,
                               ActivityLogService activityLogService) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.eventService = eventService;
        this.clubService = clubService;
        this.activityLogService = activityLogService;
    }

    @Transactional
    public RegistrationResponse registerForEvent(Long eventId, User student) {
        Event event = eventService.getEventEntity(eventId);

        if (event.getStatus() != EventStatus.PUBLISHED) {
            throw new BadRequestException("Event is not open for registration.");
        }

        if (event.getRegistrationDeadline() != null && LocalDateTime.now().isAfter(event.getRegistrationDeadline())) {
            throw new BadRequestException("The registration deadline for this event has passed.");
        }

        if (event.getRegisteredCount() >= event.getCapacity()) {
            throw new BadRequestException("This event has reached full capacity (" + event.getCapacity() + " participants).");
        }

        Optional<EventRegistration> existing = registrationRepository.findByEventAndUser(event, student);
        if (existing.isPresent()) {
            EventRegistration reg = existing.get();
            if (reg.getStatus() == RegistrationStatus.REGISTERED) {
                throw new BadRequestException("You are already registered for this event.");
            } else {
                reg.setStatus(RegistrationStatus.REGISTERED);
                reg.setAttended(false);
                reg.setCheckedInAt(null);
                reg = registrationRepository.save(reg);

                event.setRegisteredCount(event.getRegisteredCount() + 1);
                eventRepository.save(event);

                activityLogService.log(student, "EVENT_REGISTERED", "EVENT_REGISTRATION", reg.getId(),
                        "Re-registered for " + event.getTitle(), null);

                return mapToResponse(reg);
            }
        }

        EventRegistration registration = new EventRegistration(event, student);
        registration = registrationRepository.save(registration);

        event.setRegisteredCount(event.getRegisteredCount() + 1);
        eventRepository.save(event);

        activityLogService.log(student, "EVENT_REGISTERED", "EVENT_REGISTRATION", registration.getId(),
                "Registered for " + event.getTitle(), null);

        return mapToResponse(registration);
    }

    @Transactional
    public void cancelRegistration(Long registrationId, User student) {
        EventRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("EventRegistration", "id", registrationId));

        if (!registration.getUser().getId().equals(student.getId())) {
            throw new BadRequestException("You can only cancel your own registrations.");
        }

        if (registration.getStatus() == RegistrationStatus.CANCELLED) {
            return;
        }

        registration.setStatus(RegistrationStatus.CANCELLED);
        registrationRepository.save(registration);

        Event event = registration.getEvent();
        event.setRegisteredCount(Math.max(0, event.getRegisteredCount() - 1));
        eventRepository.save(event);

        activityLogService.log(student, "EVENT_REGISTRATION_CANCELLED", "EVENT_REGISTRATION", registration.getId(),
                "Cancelled registration for " + event.getTitle(), null);
    }

    @Transactional
    public RegistrationResponse updateAttendance(AttendanceUpdateRequest request, User adminUser) {
        EventRegistration reg = registrationRepository.findById(request.getRegistrationId())
                .orElseThrow(() -> new ResourceNotFoundException("EventRegistration", "id", request.getRegistrationId()));

        clubService.verifyClubAdminOrSystemAdmin(reg.getEvent().getClub(), adminUser);

        reg.setAttended(request.getAttended());
        reg.setCheckedInAt(request.getAttended() ? LocalDateTime.now() : null);
        reg = registrationRepository.save(reg);

        activityLogService.log(adminUser, "ATTENDANCE_MARKED", "EVENT_REGISTRATION", reg.getId(),
                "Attendance marked as " + request.getAttended() + " for " + reg.getUser().getFullName() + " at " + reg.getEvent().getTitle(), null);

        return mapToResponse(reg);
    }

    @Transactional(readOnly = true)
    public Page<RegistrationResponse> getEventRegistrations(Long eventId, RegistrationStatus status, Pageable pageable, User adminUser) {
        Event event = eventService.getEventEntity(eventId);
        clubService.verifyClubAdminOrSystemAdmin(event.getClub(), adminUser);
        return registrationRepository.findByEventIdAndStatus(eventId, status, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public List<RegistrationResponse> getStudentRegistrations(Long userId) {
        return registrationRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public RegistrationResponse mapToResponse(EventRegistration reg) {
        RegistrationResponse res = new RegistrationResponse();
        res.setId(reg.getId());
        res.setEventId(reg.getEvent().getId());
        res.setEventTitle(reg.getEvent().getTitle());
        res.setEventLocation(reg.getEvent().getLocation());
        res.setEventStartTime(reg.getEvent().getStartTime());
        res.setEventEndTime(reg.getEvent().getEndTime());
        res.setClubName(reg.getEvent().getClub().getName());
        res.setUserId(reg.getUser().getId());
        res.setUserName(reg.getUser().getFullName());
        res.setUserEmail(reg.getUser().getEmail());
        res.setUserStudentId(reg.getUser().getStudentId());
        res.setUserDepartment(reg.getUser().getDepartment());
        res.setStatus(reg.getStatus());
        res.setAttended(reg.isAttended());
        res.setCheckedInAt(reg.getCheckedInAt());
        res.setCreatedAt(reg.getCreatedAt());
        return res;
    }
}

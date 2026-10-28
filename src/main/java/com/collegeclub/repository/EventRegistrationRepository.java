package com.collegeclub.repository;

import com.collegeclub.entity.Event;
import com.collegeclub.entity.EventRegistration;
import com.collegeclub.entity.User;
import com.collegeclub.enums.RegistrationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {

    Optional<EventRegistration> findByEventAndUser(Event event, User user);

    boolean existsByEventAndUser(Event event, User user);

    boolean existsByEventAndUserAndStatus(Event event, User user, RegistrationStatus status);

    List<EventRegistration> findByUser(User user);

    Page<EventRegistration> findByUser(User user, Pageable pageable);

    Page<EventRegistration> findByEvent(Event event, Pageable pageable);

    @Query("SELECT er FROM EventRegistration er WHERE " +
           "er.event.id = :eventId AND " +
           "(:status IS NULL OR er.status = :status)")
    Page<EventRegistration> findByEventIdAndStatus(@Param("eventId") Long eventId,
                                                   @Param("status") RegistrationStatus status,
                                                   Pageable pageable);

    @Query("SELECT er FROM EventRegistration er WHERE er.user.id = :userId ORDER BY er.createdAt DESC")
    List<EventRegistration> findByUserId(@Param("userId") Long userId);

    long countByEventAndStatus(Event event, RegistrationStatus status);

    long countByEventAndAttendedTrue(Event event);

    long countByAttendedTrue();

    @Query("SELECT er FROM EventRegistration er WHERE er.event.club.id = :clubId")
    List<EventRegistration> findByClubId(@Param("clubId") Long clubId);
}

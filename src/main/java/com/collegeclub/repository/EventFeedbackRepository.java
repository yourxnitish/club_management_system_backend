package com.collegeclub.repository;

import com.collegeclub.entity.Event;
import com.collegeclub.entity.EventFeedback;
import com.collegeclub.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventFeedbackRepository extends JpaRepository<EventFeedback, Long> {

    Optional<EventFeedback> findByEventAndUser(Event event, User user);

    boolean existsByEventAndUser(Event event, User user);

    List<EventFeedback> findByEvent(Event event);

    Page<EventFeedback> findByEvent(Event event, Pageable pageable);

    @Query("SELECT ef FROM EventFeedback ef WHERE ef.event.club.id = :clubId ORDER BY ef.createdAt DESC")
    Page<EventFeedback> findByClubId(@Param("clubId") Long clubId, Pageable pageable);

    @Query("SELECT AVG(ef.rating) FROM EventFeedback ef WHERE ef.event.id = :eventId")
    Double getAverageRatingForEvent(@Param("eventId") Long eventId);

    @Query("SELECT AVG(ef.rating) FROM EventFeedback ef WHERE ef.event.club.id = :clubId")
    Double getAverageRatingForClub(@Param("clubId") Long clubId);

    @Query("SELECT AVG(ef.rating) FROM EventFeedback ef")
    Double getOverallAverageRating();
}

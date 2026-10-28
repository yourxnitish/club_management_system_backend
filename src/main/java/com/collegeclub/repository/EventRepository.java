package com.collegeclub.repository;

import com.collegeclub.entity.Club;
import com.collegeclub.entity.Event;
import com.collegeclub.enums.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findBySlug(String slug);

    List<Event> findByClub(Club club);

    Page<Event> findByClub(Club club, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE " +
           "(:clubId IS NULL OR e.club.id = :clubId) AND " +
           "(:status IS NULL OR e.status = :status) AND " +
           "(:upcoming IS NULL OR (:upcoming = true AND e.startTime >= :now) OR (:upcoming = false AND e.startTime < :now)) AND " +
           "(:search IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.location) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Event> searchEvents(@Param("clubId") Long clubId,
                             @Param("status") EventStatus status,
                             @Param("upcoming") Boolean upcoming,
                             @Param("now") LocalDateTime now,
                             @Param("search") String search,
                             Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.status = 'PUBLISHED' AND e.startTime >= :now ORDER BY e.startTime ASC")
    List<Event> findUpcomingPublishedEvents(@Param("now") LocalDateTime now, Pageable pageable);

    long countByStatus(EventStatus status);

    @Query("SELECT COUNT(e) FROM Event e WHERE e.startTime >= :now AND e.status = 'PUBLISHED'")
    long countUpcomingEvents(@Param("now") LocalDateTime now);
}

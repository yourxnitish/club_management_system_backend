package com.collegeclub.repository;

import com.collegeclub.entity.Announcement;
import com.collegeclub.entity.Club;
import com.collegeclub.enums.Priority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findByClub(Club club);

    Page<Announcement> findByClub(Club club, Pageable pageable);

    @Query("SELECT a FROM Announcement a WHERE " +
           "(:clubId IS NULL OR a.club.id = :clubId) AND " +
           "(:priority IS NULL OR a.priority = :priority) AND " +
           "(:isPublic IS NULL OR a.isPublic = :isPublic)")
    Page<Announcement> searchAnnouncements(@Param("clubId") Long clubId,
                                           @Param("priority") Priority priority,
                                           @Param("isPublic") Boolean isPublic,
                                           Pageable pageable);

    @Query("SELECT a FROM Announcement a WHERE a.isPublic = true OR a.club.id IN :clubIds ORDER BY a.createdAt DESC")
    List<Announcement> findFeedForUserClubs(@Param("clubIds") List<Long> clubIds, Pageable pageable);

    @Query("SELECT a FROM Announcement a WHERE a.isPublic = true ORDER BY a.createdAt DESC")
    List<Announcement> findPublicAnnouncements(Pageable pageable);
}

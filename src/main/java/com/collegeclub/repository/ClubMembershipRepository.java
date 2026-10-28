package com.collegeclub.repository;

import com.collegeclub.entity.Club;
import com.collegeclub.entity.ClubMembership;
import com.collegeclub.entity.User;
import com.collegeclub.enums.MembershipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubMembershipRepository extends JpaRepository<ClubMembership, Long> {

    Optional<ClubMembership> findByClubAndUser(Club club, User user);

    boolean existsByClubAndUser(Club club, User user);

    boolean existsByClubAndUserAndStatus(Club club, User user, MembershipStatus status);

    List<ClubMembership> findByUser(User user);

    Page<ClubMembership> findByUser(User user, Pageable pageable);

    Page<ClubMembership> findByClub(Club club, Pageable pageable);

    @Query("SELECT cm FROM ClubMembership cm WHERE " +
           "cm.club.id = :clubId AND " +
           "(:status IS NULL OR cm.status = :status)")
    Page<ClubMembership> findByClubIdAndStatus(@Param("clubId") Long clubId,
                                               @Param("status") MembershipStatus status,
                                               Pageable pageable);

    long countByClubAndStatus(Club club, MembershipStatus status);

    long countByStatus(MembershipStatus status);

    @Query("SELECT cm FROM ClubMembership cm WHERE cm.user.id = :userId AND (:status IS NULL OR cm.status = :status)")
    List<ClubMembership> findByUserIdAndOptionalStatus(@Param("userId") Long userId, @Param("status") MembershipStatus status);
}

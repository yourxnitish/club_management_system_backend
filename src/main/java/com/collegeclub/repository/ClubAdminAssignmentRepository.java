package com.collegeclub.repository;

import com.collegeclub.entity.Club;
import com.collegeclub.entity.ClubAdminAssignment;
import com.collegeclub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubAdminAssignmentRepository extends JpaRepository<ClubAdminAssignment, Long> {

    List<ClubAdminAssignment> findByClub(Club club);

    List<ClubAdminAssignment> findByUser(User user);

    Optional<ClubAdminAssignment> findByClubAndUser(Club club, User user);

    boolean existsByClubAndUser(Club club, User user);

    void deleteByClubAndUser(Club club, User user);

    @Query("SELECT caa.club.id FROM ClubAdminAssignment caa WHERE caa.user.id = :userId")
    List<Long> findClubIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT caa.user FROM ClubAdminAssignment caa WHERE caa.club.id = :clubId")
    List<User> findAdminsByClubId(@Param("clubId") Long clubId);
}

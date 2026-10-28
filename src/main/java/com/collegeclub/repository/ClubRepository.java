package com.collegeclub.repository;

import com.collegeclub.entity.Club;
import com.collegeclub.enums.ClubStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {

    Optional<Club> findBySlug(String slug);

    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    List<Club> findByStatus(ClubStatus status);

    @Query("SELECT c FROM Club c WHERE " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:category IS NULL OR LOWER(c.category) = LOWER(:category)) AND " +
           "(:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.shortDescription) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Club> searchClubs(@Param("status") ClubStatus status,
                           @Param("category") String category,
                           @Param("search") String search,
                           Pageable pageable);

    @Query("SELECT DISTINCT c.category FROM Club c WHERE c.status = 'ACTIVE' ORDER BY c.category ASC")
    List<String> findDistinctCategories();

    long countByStatus(ClubStatus status);
}

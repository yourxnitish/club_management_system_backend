package com.collegeclub.repository;

import com.collegeclub.entity.User;
import com.collegeclub.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByStudentId(String studentId);

    Page<User> findByRole(Role role, Pageable pageable);

    @Query("SELECT u FROM User u WHERE " +
           "(:role IS NULL OR u.role = :role) AND " +
           "(:active IS NULL OR u.active = :active) AND " +
           "(:search IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.studentId) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> searchUsers(@Param("role") Role role,
                           @Param("active") Boolean active,
                           @Param("search") String search,
                           Pageable pageable);

    long countByRole(Role role);

    long countByActiveTrue();
}

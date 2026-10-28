package com.collegeclub.service;

import com.collegeclub.dto.request.UserUpdateRequest;
import com.collegeclub.dto.response.UserResponse;
import com.collegeclub.entity.User;
import com.collegeclub.enums.Role;
import com.collegeclub.exception.BadRequestException;
import com.collegeclub.exception.ResourceNotFoundException;
import com.collegeclub.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;

    public UserService(UserRepository userRepository, ActivityLogService activityLogService) {
        this.userRepository = userRepository;
        this.activityLogService = activityLogService;
    }

    @Transactional(readOnly = true)
    public User getUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    @Transactional(readOnly = true)
    public User getUserEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return mapToResponse(getUserEntity(id));
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        return mapToResponse(getUserEntityByEmail(email));
    }

    @Transactional
    public UserResponse updateUserProfile(Long userId, UserUpdateRequest request) {
        User user = getUserEntity(userId);

        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            user.setFirstName(request.getFirstName().trim());
        }
        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            user.setLastName(request.getLastName().trim());
        }
        if (request.getStudentId() != null) {
            String newStuId = request.getStudentId().trim();
            if (!newStuId.equalsIgnoreCase(user.getStudentId()) && userRepository.existsByStudentId(newStuId)) {
                throw new BadRequestException("Student ID " + newStuId + " is already associated with another account.");
            }
            user.setStudentId(newStuId);
        }
        if (request.getDepartment() != null) {
            user.setDepartment(request.getDepartment().trim());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone().trim());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl().trim());
        }

        user = userRepository.save(user);
        activityLogService.log(user, "USER_PROFILE_UPDATED", "USER", user.getId(), "User profile details updated", null);

        return mapToResponse(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsers(Role role, Boolean active, String search, Pageable pageable) {
        return userRepository.searchUsers(role, active, search, pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public UserResponse toggleUserActive(Long targetUserId, boolean active, User adminUser) {
        if (adminUser.getId().equals(targetUserId)) {
            throw new BadRequestException("You cannot deactivate your own administrative account.");
        }

        User targetUser = getUserEntity(targetUserId);
        targetUser.setActive(active);
        targetUser = userRepository.save(targetUser);

        activityLogService.log(adminUser, active ? "USER_ACTIVATED" : "USER_DEACTIVATED", "USER", targetUserId,
                "Account status toggled for " + targetUser.getEmail(), null);

        return mapToResponse(targetUser);
    }

    @Transactional
    public UserResponse changeUserRole(Long targetUserId, Role newRole, User adminUser) {
        User targetUser = getUserEntity(targetUserId);
        targetUser.setRole(newRole);
        targetUser = userRepository.save(targetUser);

        activityLogService.log(adminUser, "USER_ROLE_CHANGED", "USER", targetUserId,
                "Role changed to " + newRole.name() + " for " + targetUser.getEmail(), null);

        return mapToResponse(targetUser);
    }

    public UserResponse mapToResponse(User user) {
        UserResponse res = new UserResponse();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setFirstName(user.getFirstName());
        res.setLastName(user.getLastName());
        res.setFullName(user.getFullName());
        res.setStudentId(user.getStudentId());
        res.setDepartment(user.getDepartment());
        res.setPhone(user.getPhone());
        res.setAvatarUrl(user.getAvatarUrl());
        res.setRole(user.getRole());
        res.setActive(user.isActive());
        res.setCreatedAt(user.getCreatedAt());
        return res;
    }
}

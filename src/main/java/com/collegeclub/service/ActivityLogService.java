package com.collegeclub.service;

import com.collegeclub.dto.response.ActivityLogResponse;
import com.collegeclub.entity.ActivityLog;
import com.collegeclub.entity.User;
import com.collegeclub.repository.ActivityLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @Transactional
    public void log(User user, String action, String entityType, Long entityId, String details, String ipAddress) {
        ActivityLog log = new ActivityLog(user, action, entityType, entityId, details, ipAddress);
        activityLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<ActivityLogResponse> getRecentLogs() {
        return activityLogRepository.findTop20ByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ActivityLogResponse> getAllLogs(Pageable pageable) {
        return activityLogRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::mapToResponse);
    }

    public ActivityLogResponse mapToResponse(ActivityLog log) {
        ActivityLogResponse res = new ActivityLogResponse();
        res.setId(log.getId());
        if (log.getUser() != null) {
            res.setUserId(log.getUser().getId());
            res.setUserName(log.getUser().getFullName());
            res.setUserEmail(log.getUser().getEmail());
        }
        res.setAction(log.getAction());
        res.setEntityType(log.getEntityType());
        res.setEntityId(log.getEntityId());
        res.setDetails(log.getDetails());
        res.setIpAddress(log.getIpAddress());
        res.setCreatedAt(log.getCreatedAt());
        return res;
    }
}

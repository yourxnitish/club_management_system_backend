package com.collegeclub.dto.response;

import com.collegeclub.enums.ClubStatus;
import java.time.LocalDateTime;
import java.util.List;

public class ClubResponse {

    private Long id;
    private String name;
    private String slug;
    private String shortDescription;
    private String description;
    private String category;
    private String bannerUrl;
    private String logoUrl;
    private String contactEmail;
    private String meetingSchedule;
    private String location;
    private ClubStatus status;
    private long memberCount;
    private long eventCount;
    private Double averageRating;
    private List<UserResponse> admins;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ClubResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getMeetingSchedule() {
        return meetingSchedule;
    }

    public void setMeetingSchedule(String meetingSchedule) {
        this.meetingSchedule = meetingSchedule;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ClubStatus getStatus() {
        return status;
    }

    public void setStatus(ClubStatus status) {
        this.status = status;
    }

    public long getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(long memberCount) {
        this.memberCount = memberCount;
    }

    public long getEventCount() {
        return eventCount;
    }

    public void setEventCount(long eventCount) {
        this.eventCount = eventCount;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public List<UserResponse> getAdmins() {
        return admins;
    }

    public void setAdmins(List<UserResponse> admins) {
        this.admins = admins;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

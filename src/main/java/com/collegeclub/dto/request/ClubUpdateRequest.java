package com.collegeclub.dto.request;

import com.collegeclub.enums.ClubStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class ClubUpdateRequest {

    @Size(min = 3, max = 150)
    private String name;

    @Size(max = 255)
    private String shortDescription;

    private String description;
    private String category;
    private String bannerUrl;
    private String logoUrl;

    @Email
    private String contactEmail;

    private String meetingSchedule;
    private String location;
    private ClubStatus status;

    public ClubUpdateRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}

package com.collegeclub.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ClubCreateRequest {

    @NotBlank(message = "Club name is required")
    @Size(min = 3, max = 150, message = "Name must be between 3 and 150 characters")
    private String name;

    @NotBlank(message = "Short description is required")
    @Size(max = 255, message = "Short description must be at most 255 characters")
    private String shortDescription;

    @NotBlank(message = "Full description is required")
    private String description;

    @NotBlank(message = "Category is required")
    private String category;

    private String bannerUrl;
    private String logoUrl;

    @NotBlank(message = "Contact email is required")
    @Email(message = "Contact email format is invalid")
    private String contactEmail;

    private String meetingSchedule;
    private String location;

    public ClubCreateRequest() {
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
}

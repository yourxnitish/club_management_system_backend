package com.collegeclub.dto.response;

import java.util.List;
import java.util.Map;

public class DashboardStatsResponse {

    // Common and Role-specific counters
    private long totalClubs;
    private long activeClubs;
    private long totalEvents;
    private long upcomingEvents;
    private long totalMembers;
    private long totalRegistrations;
    private long totalAttendance;
    private double overallAttendanceRate;
    private double averageFeedbackRating;
    private long totalStudents;

    // Student specific
    private long myJoinedClubsCount;
    private long myRegisteredEventsCount;
    private long myAttendedEventsCount;
    private long myPendingRequestsCount;

    // Club Admin specific
    private long myManagedClubsCount;
    private long pendingMembershipRequestsCount;

    // Charts & breakdowns
    private Map<String, Long> clubsByCategory;
    private Map<String, Long> eventsByStatus;
    private List<Map<String, Object>> monthlyRegistrationsTrend;
    private List<ClubResponse> topClubs;
    private List<EventResponse> upcomingEventsList;
    private List<ActivityLogResponse> recentActivities;

    public DashboardStatsResponse() {
    }

    public long getTotalClubs() {
        return totalClubs;
    }

    public void setTotalClubs(long totalClubs) {
        this.totalClubs = totalClubs;
    }

    public long getActiveClubs() {
        return activeClubs;
    }

    public void setActiveClubs(long activeClubs) {
        this.activeClubs = activeClubs;
    }

    public long getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(long totalEvents) {
        this.totalEvents = totalEvents;
    }

    public long getUpcomingEvents() {
        return upcomingEvents;
    }

    public void setUpcomingEvents(long upcomingEvents) {
        this.upcomingEvents = upcomingEvents;
    }

    public long getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(long totalMembers) {
        this.totalMembers = totalMembers;
    }

    public long getTotalRegistrations() {
        return totalRegistrations;
    }

    public void setTotalRegistrations(long totalRegistrations) {
        this.totalRegistrations = totalRegistrations;
    }

    public long getTotalAttendance() {
        return totalAttendance;
    }

    public void setTotalAttendance(long totalAttendance) {
        this.totalAttendance = totalAttendance;
    }

    public double getOverallAttendanceRate() {
        return overallAttendanceRate;
    }

    public void setOverallAttendanceRate(double overallAttendanceRate) {
        this.overallAttendanceRate = overallAttendanceRate;
    }

    public double getAverageFeedbackRating() {
        return averageFeedbackRating;
    }

    public void setAverageFeedbackRating(double averageFeedbackRating) {
        this.averageFeedbackRating = averageFeedbackRating;
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getMyJoinedClubsCount() {
        return myJoinedClubsCount;
    }

    public void setMyJoinedClubsCount(long myJoinedClubsCount) {
        this.myJoinedClubsCount = myJoinedClubsCount;
    }

    public long getMyRegisteredEventsCount() {
        return myRegisteredEventsCount;
    }

    public void setMyRegisteredEventsCount(long myRegisteredEventsCount) {
        this.myRegisteredEventsCount = myRegisteredEventsCount;
    }

    public long getMyAttendedEventsCount() {
        return myAttendedEventsCount;
    }

    public void setMyAttendedEventsCount(long myAttendedEventsCount) {
        this.myAttendedEventsCount = myAttendedEventsCount;
    }

    public long getMyPendingRequestsCount() {
        return myPendingRequestsCount;
    }

    public void setMyPendingRequestsCount(long myPendingRequestsCount) {
        this.myPendingRequestsCount = myPendingRequestsCount;
    }

    public long getMyManagedClubsCount() {
        return myManagedClubsCount;
    }

    public void setMyManagedClubsCount(long myManagedClubsCount) {
        this.myManagedClubsCount = myManagedClubsCount;
    }

    public long getPendingMembershipRequestsCount() {
        return pendingMembershipRequestsCount;
    }

    public void setPendingMembershipRequestsCount(long pendingMembershipRequestsCount) {
        this.pendingMembershipRequestsCount = pendingMembershipRequestsCount;
    }

    public Map<String, Long> getClubsByCategory() {
        return clubsByCategory;
    }

    public void setClubsByCategory(Map<String, Long> clubsByCategory) {
        this.clubsByCategory = clubsByCategory;
    }

    public Map<String, Long> getEventsByStatus() {
        return eventsByStatus;
    }

    public void setEventsByStatus(Map<String, Long> eventsByStatus) {
        this.eventsByStatus = eventsByStatus;
    }

    public List<Map<String, Object>> getMonthlyRegistrationsTrend() {
        return monthlyRegistrationsTrend;
    }

    public void setMonthlyRegistrationsTrend(List<Map<String, Object>> monthlyRegistrationsTrend) {
        this.monthlyRegistrationsTrend = monthlyRegistrationsTrend;
    }

    public List<ClubResponse> getTopClubs() {
        return topClubs;
    }

    public void setTopClubs(List<ClubResponse> topClubs) {
        this.topClubs = topClubs;
    }

    public List<EventResponse> getUpcomingEventsList() {
        return upcomingEventsList;
    }

    public void setUpcomingEventsList(List<EventResponse> upcomingEventsList) {
        this.upcomingEventsList = upcomingEventsList;
    }

    public List<ActivityLogResponse> getRecentActivities() {
        return recentActivities;
    }

    public void setRecentActivities(List<ActivityLogResponse> recentActivities) {
        this.recentActivities = recentActivities;
    }
}

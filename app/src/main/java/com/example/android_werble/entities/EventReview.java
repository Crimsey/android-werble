package com.example.android_werble.entities;

import com.squareup.moshi.Json;

public class EventReview {

    @Json(name = "event_review_id")
    private Integer eventReviewId;
    @Json(name = "content")
    private String content;
    @Json(name = "rating")
    private Integer rating;
    @Json(name = "is_active")
    private Integer isActive;
    @Json(name = "event_participant_id")
    private Integer eventParticipantId;
    @Json(name = "event_id")
    private Integer eventId;
    @Json(name = "deleted_at")
    private Object deletedAt;
    @Json(name = "created_at")
    private String createdAt;
    @Json(name = "updated_at")
    private String updatedAt;
    @Json(name = "login")
    private String login;

    public Integer getEventReviewId() {
        return eventReviewId;
    }

    public void setEventReviewId(Integer eventReviewId) {
        this.eventReviewId = eventReviewId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Integer getEventParticipantId() {
        return eventParticipantId;
    }

    public void setEventParticipantId(Integer eventParticipantId) {
        this.eventParticipantId = eventParticipantId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

}
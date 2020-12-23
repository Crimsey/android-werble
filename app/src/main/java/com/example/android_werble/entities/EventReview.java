package com.example.android_werble.entities;

import com.squareup.moshi.Json;

public class EventReview {

    @Json(name = "content")
    private String content;
    @Json(name = "rating")
    private Integer rating;
    @Json(name = "event_id")
    private Integer eventId;

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

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

}

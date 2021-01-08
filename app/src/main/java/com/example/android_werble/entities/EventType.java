package com.example.android_werble.entities;

import androidx.annotation.NonNull;

import com.squareup.moshi.Json;

public class EventType {

    @Json(name = "event_type_id")
    private Integer eventTypeId;
    @Json(name = "event_type_name")
    private String eventTypeName;

    public Integer getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(Integer eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public String getEventTypeName() {
        return eventTypeName;
    }

    public void setEventTypeName(String eventTypeName) {
        this.eventTypeName = eventTypeName;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getEventTypeName();
    }
}
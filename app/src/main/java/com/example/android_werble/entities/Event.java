package com.example.android_werble.entities;

import com.google.common.primitives.UnsignedInteger;
import com.squareup.moshi.Json;

import java.util.Date;

public class Event {

    @Json(name = "event_id")
    private Integer eventId;
    @Json(name = "name")
    private String name;
    @Json(name = "location")
    private String location;
    @Json(name = "zip_code")
    private String zipCode;
    @Json(name = "street_name")
    private String streetName;
    @Json(name = "house_number")
    private String houseNumber;
    @Json(name = "longitude")
    private Double longitude;
    @Json(name = "latitude")
    private Double latitude;
    @Json(name = "description")
    private String description;
    @Json(name = "datetime")
    private String datetime;
    @Json(name = "is_active")
    private Integer isActive;
    @Json(name = "event_visibility_level_id")
    private Integer eventVisibilityLevelId;
    @Json(name = "event_status_id")
    private Integer eventStatusId;
    @Json(name = "event_creator_id")
    private Integer eventCreatorId;
    @Json(name = "event_type_id")
    private Integer eventTypeId;
    @Json(name = "deleted_at")
    private Object deletedAt;
    @Json(name = "created_at")
    private String createdAt;
    @Json(name = "updated_at")
    private String updatedAt;

    @Json(name = "disance")
    private Double distance;

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }



    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Integer getEventVisibilityLevelId() {
        return eventVisibilityLevelId;
    }

    public void setEventVisibilityLevelId(Integer eventVisibilityLevelId) {
        this.eventVisibilityLevelId = eventVisibilityLevelId;
    }

    public Integer getEventStatusId() {
        return eventStatusId;
    }

    public void setEventStatusId(Integer eventStatusId) {
        this.eventStatusId = eventStatusId;
    }

    public Integer getEventCreatorId() {
        return eventCreatorId;
    }

    public void setEventCreatorId(Integer eventCreatorId) {
        this.eventCreatorId = eventCreatorId;
    }

    public Integer getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(Integer eventTypeId) {
        this.eventTypeId = eventTypeId;
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

}



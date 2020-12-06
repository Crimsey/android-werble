package com.example.android_werble.entities;

import com.squareup.moshi.Json;

public class User {

    @Json(name = "user_id")
    private Integer userId;
    @Json(name = "login")
    private String login;
    @Json(name = "email")
    private String email;
    @Json(name = "email_verified_at")
    private Object emailVerifiedAt;
    @Json(name = "two_factor_secret")
    private Object twoFactorSecret;
    @Json(name = "two_factor_recovery_codes")
    private Object twoFactorRecoveryCodes;
    @Json(name = "first_name")
    private String firstName;
    @Json(name = "last_name")
    private String lastName;
    @Json(name = "birth_date")
    private String birthDate;
    @Json(name = "description")
    private String description;
    @Json(name = "longitude")
    private Double longitude;
    @Json(name = "latitude")
    private Double latitude;
    @Json(name = "is_admin")
    private Integer isAdmin;
    @Json(name = "is_active")
    private Integer isActive;
    @Json(name = "deleted_at")
    private Object deletedAt;
    @Json(name = "created_at")
    private String createdAt;
    @Json(name = "updated_at")
    private String updatedAt;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Object getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public void setEmailVerifiedAt(Object emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public Object getTwoFactorSecret() {
        return twoFactorSecret;
    }

    public void setTwoFactorSecret(Object twoFactorSecret) {
        this.twoFactorSecret = twoFactorSecret;
    }

    public Object getTwoFactorRecoveryCodes() {
        return twoFactorRecoveryCodes;
    }

    public void setTwoFactorRecoveryCodes(Object twoFactorRecoveryCodes) {
        this.twoFactorRecoveryCodes = twoFactorRecoveryCodes;
    }

    public Object getFirstName() {
        return firstName;
    } //

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Object getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Object getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Object getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Integer getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Integer isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
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
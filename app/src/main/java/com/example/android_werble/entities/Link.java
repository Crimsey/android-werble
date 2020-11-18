package com.example.android_werble.entities;

import com.squareup.moshi.Json;

public class Link {

    @Json(name = "url")
    private Object url;
    @Json(name = "label")
    private String label;
    @Json(name = "active")
    private Boolean active;

    public Object getUrl() {
        return url;
    }

    public void setUrl(Object url) {
        this.url = url;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

}

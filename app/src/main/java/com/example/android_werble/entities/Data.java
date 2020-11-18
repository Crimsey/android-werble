package com.example.android_werble.entities;

import com.example.android_werble.TokenManager;
import com.squareup.moshi.Json;

import java.util.List;

public class Data <T>  {

    @Json(name = "data")
    private List<T> data = null;
    @Json(name = "links")
    private Links links;
    @Json(name = "meta")
    private Meta meta;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}

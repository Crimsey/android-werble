package com.example.android_werble.entities;

import java.util.List;
import com.squareup.moshi.Json;

public class Meta {

    @Json(name = "current_page")
    private Integer currentPage;
    @Json(name = "from")
    private Integer from;
    @Json(name = "last_page")
    private Integer lastPage;
    @Json(name = "links")
    private List<Link> links = null;
    @Json(name = "path")
    private String path;
    @Json(name = "per_page")
    private Integer perPage;
    @Json(name = "to")
    private Integer to;
    @Json(name = "total")
    private Integer total;

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getLastPage() {
        return lastPage;
    }

    public void setLastPage(Integer lastPage) {
        this.lastPage = lastPage;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getPerPage() {
        return perPage;
    }

    public void setPerPage(Integer perPage) {
        this.perPage = perPage;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

}
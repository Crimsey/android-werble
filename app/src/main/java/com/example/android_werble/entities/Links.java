package com.example.android_werble.entities;

import com.squareup.moshi.Json;

public class Links {

    @Json(name = "first")
    private String first;
    @Json(name = "last")
    private String last;
    @Json(name = "prev")
    private Object prev;
    @Json(name = "next")
    private Object next;

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public Object getPrev() {
        return prev;
    }

    public void setPrev(Object prev) {
        this.prev = prev;
    }

    public Object getNext() {
        return next;
    }

    public void setNext(Object next) {
        this.next = next;
    }

}
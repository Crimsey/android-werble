package com.example.android_werble.entities;

import com.google.common.primitives.UnsignedInteger;

import java.util.Date;

public class Event {

    int event_id;
    String name;
    String location;
    String zip_code;
    String street_name;
    String house_number;
    Double longitude;
    Double latitude;
    String description;
    Date datetime;
    Boolean is_active;
    UnsignedInteger event_visibility_level_id;
    UnsignedInteger event_status_id;
    UnsignedInteger event_creator_id;
    UnsignedInteger event_type_id;

    public String getName(){
        return name;
    }
    

}

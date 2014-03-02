package com.samteladze.vzradio.android.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Event {
    public String id;

    public String alias;

    @JsonProperty("locid")
    public String locationId;

    public String venue;

    public String title;

    @JsonProperty("datdescription")
    public String description;

    @JsonProperty("start_time")
    public String startTime;

    @JsonProperty("end_time")
    public String endTime;

    @JsonProperty("img")
    public List<EventImageResources> imagesResources;

    @JsonProperty("url")
    public String eventUri;
}

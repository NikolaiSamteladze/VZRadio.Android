package com.samteladze.vzradio.android.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by user441 on 2/9/14.
 */
public class EventImageResources {
    @JsonProperty("thumb_small")
    public String smallThumbUri;

    @JsonProperty("thumb_medium")
    public String mediumThumbUri;

    @JsonProperty("orig")
    public String originalUri;
}

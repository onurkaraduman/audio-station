package com.j4mbackend.converter.type;

/**
 * Video type
 * Created by onur on 16.03.17.
 */
public enum VideoType {
    THREE_GP("3GP"),
    FLV("FLV"),
    WEBM("WEBM"),
    MP4("MP4");

    public String name;

    VideoType(String name) {
        this.name = name;
    }
}

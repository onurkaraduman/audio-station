package com.onrkrdmn.converter.data;

import com.onrkrdmn.converter.model.VideoMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * Video type list
 * Created by onur on 16.03.17.
 */
public final class VideoTypeList {

    /**
     * The types of videos
     */
    public static Map<String, VideoMeta> TYPE_MAP = new HashMap<>();

    static {
        TYPE_MAP.put("13", new VideoMeta("13", "3GP", "Low Quality - 176x144"));
        TYPE_MAP.put("17", new VideoMeta("17", "3GP", "Medium Quality - 176x144"));
        TYPE_MAP.put("36", new VideoMeta("36", "3GP", "High Quality - 320x240"));
        TYPE_MAP.put("5", new VideoMeta("5", "FLV", "Low Quality - 400x226"));
        TYPE_MAP.put("6", new VideoMeta("6", "FLV", "Medium Quality - 640x360"));
        TYPE_MAP.put("34", new VideoMeta("34", "FLV", "Medium Quality - 640x360"));
        TYPE_MAP.put("35", new VideoMeta("35", "FLV", "High Quality - 854x480"));
        TYPE_MAP.put("43", new VideoMeta("43", "WEBM", "Low Quality - 640x360"));
        TYPE_MAP.put("44", new VideoMeta("44", "WEBM", "Medium Quality - 854x480"));
        TYPE_MAP.put("45", new VideoMeta("45", "WEBM", "High Quality - 1280x720"));
        TYPE_MAP.put("18", new VideoMeta("18", "MP4", "Medium Quality - 480x360"));
        TYPE_MAP.put("22", new VideoMeta("22", "MP4", "High Quality - 1280x720"));
        TYPE_MAP.put("37", new VideoMeta("37", "MP4", "High Quality - 1920x1080"));
        TYPE_MAP.put("33", new VideoMeta("38", "MP4", "High Quality - 4096x230"));
    }
}

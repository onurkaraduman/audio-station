package com.onrkrdmn.converter.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Video meta data class
 * Created by onur on 16.03.17.
 */
@Getter
@Setter
@AllArgsConstructor
public class VideoMeta {
    private String num;
    private String type;
    private String ext;
}

package com.onrkrdmn.converter;

import com.onrkrdmn.converter.model.Video;

import java.io.IOException;
import java.util.List;

/**
 * Link converter interface
 * The URL is converted to video url
 * Created by onur on 16.03.17.
 */
public interface UrlConverter {
    public List<Video> convert(String url) throws IOException;
}

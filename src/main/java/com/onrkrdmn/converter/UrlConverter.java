package com.onrkrdmn.converter;

import com.onrkrdmn.converter.model.Video;

import java.io.IOException;
import java.util.List;

/**
 * Link linkconverter interface
 * Created by onur on 16.03.17.
 */
public interface UrlConverter {
    public List<Video> convert(String url) throws IOException;
}

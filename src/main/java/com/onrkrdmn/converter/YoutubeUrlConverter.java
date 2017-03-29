package com.onrkrdmn.converter;

import com.onrkrdmn.converter.data.VideoTypeList;
import com.onrkrdmn.converter.model.Video;
import com.onrkrdmn.converter.model.VideoMeta;
import lombok.extern.log4j.Log4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Youtube video url converter
 * Created by onur on 16.03.17.
 */
@Log4j
public class YoutubeUrlConverter implements UrlConverter {

    @Override
    public List<Video> convert(String yUrl) throws IOException {
        if (yUrl == null) {
            throw new IllegalArgumentException("Url cannot be null");
        }

        // Remove any query params in query string after the watch?v=<vid>
        // in
        // e.g.
        // http://www.youtube.com/watch?v=0RUPACpf8Vs&feature=youtube_gdata_player
        int andIdx = yUrl.indexOf('&');
        if (andIdx >= 0) {
            yUrl = yUrl.substring(0, andIdx);
        }

        String html = getHtmlResponse(yUrl);

        checkHtmlSource(html);

        Pattern p = Pattern.compile("stream_map\":\"(.*?)?\"");
        // Pattern p = Pattern.compile("/stream_map=(.[^&]*?)\"/");
        Matcher m = p.matcher(html);
        List<String> matches = new ArrayList<String>();
        while (m.find()) {
            matches.add(m.group());
        }

        if (matches.size() != 1) {
            log.error("Found zero or too many stream maps.");
            throw new IllegalArgumentException("Nothing found");
        }

        String urls[] = matches.get(0).split(",");
        Map<String, String> foundArray = new HashMap<String, String>();
        for (String ppUrl : urls) {
            String url = URLDecoder.decode(ppUrl, "UTF-8");

            Pattern p1 = Pattern.compile("itag=([0-9]+?)[&]");
            Matcher m1 = p1.matcher(url);
            String itag = null;
            if (m1.find()) {
                itag = m1.group(1);
            }

            Pattern p2 = Pattern.compile("signature=(.*?)[&]");
            Matcher m2 = p2.matcher(url);
            String sig = null;
            if (m2.find()) {
                sig = m2.group(1);
            }

            Pattern p3 = Pattern.compile("url=(.*?)[&]");
            Matcher m3 = p3.matcher(ppUrl);
            String um = null;
            if (m3.find()) {
                um = m3.group(1);
            }

            if (itag != null && sig != null && um != null) {
                foundArray.put(itag, URLDecoder.decode(um, "UTF-8") + "&" + "signature=" + sig);
            }
        }

        if (foundArray.size() == 0) {
            log.error("Couldn't find any URLs and corresponding signatures");
            throw new IllegalArgumentException("Couldn't find any URLs and corresponding signatures");
        }


        return mapToVideo(foundArray);
    }

    /**
     * Get the response from url
     *
     * @param url
     * @return
     */
    private String getHtmlResponse(String url) throws IOException {
        URL u = new URL(url);
        InputStream is = u.openStream(); // throws an IOException
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder str = new StringBuilder();

        String line = null;

        while ((line = reader.readLine()) != null) {
            str.append(line.replace("\\u0026", "&"));
        }
        is.close();
        return str.toString();
    }

    private boolean checkHtmlSource(String html) {
        // Parse the HTML response and extract the streaming URIs
        if (html.contains("verify-age-thumb")) {
            log.error("YouTube is asking for age verification. We can't handle that sorry.");
            throw new IllegalStateException("YouTube is asking for age verification");
        }

        if (html.contains("das_captcha")) {
            log.error("Captcha found, please try with different IP address.");
            throw new IllegalArgumentException("Captcha found, please try with different IP address");
        }
        return true;
    }

    /**
     * Convert map found array to video list
     *
     * @param foundArray
     * @return
     */
    private List<Video> mapToVideo(Map<String, String> foundArray) {
        List<Video> videos = new ArrayList<Video>();

        for (String format : VideoTypeList.TYPE_MAP.keySet()) {
            VideoMeta meta = VideoTypeList.TYPE_MAP.get(format);

            if (foundArray.containsKey(format)) {
                Video newVideo = new Video(meta.getExt(), meta.getType(), foundArray.get(format));
                videos.add(newVideo);
                log.debug("YouTube Video streaming details: ext:" + newVideo.getExt() + ", type:" + newVideo.getType()
                        + ", url:" + newVideo.getUrl());
            }
        }
        return videos;
    }
}

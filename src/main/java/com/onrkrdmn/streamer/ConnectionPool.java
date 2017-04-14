package com.onrkrdmn.streamer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onrkrdmn.converter.UrlConverter;
import com.onrkrdmn.converter.YoutubeUrlConverter;
import com.onrkrdmn.converter.model.Video;
import com.onrkrdmn.core.AudioBuffer;
import com.onrkrdmn.fetcher.VideoFetcher;
import com.onrkrdmn.fetcher.YoutubeVideoFetcher;
import com.onrkrdmn.streamer.constant.StreamerConst;
import com.onrkrdmn.validator.UrlValidator;
import com.onrkrdmn.validator.YoutubeUrlValidator;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

/**
 * Websocket connection reciever
 *
 * @author Onur Karaduman
 * @since 02.04.17
 */
@Service
@Log4j
public class ConnectionPool extends AbstractVerticle {

    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        Vertx.vertx().deployVerticle(ConnectionPool.class.getName());
        this.objectMapper = new ObjectMapper();
    }


    @Override
    public void start() throws Exception {

        // mp3 streamer web socket
        vertx.createHttpServer().websocketHandler(ws -> {
            if (!ws.path().equals("/stream")) {
                ws.reject();
            }
            ws.handler(buffer -> {
                String url = buffer.toString();
                log.info("Url>>>>>>>>>>" + url);
                UrlValidator validator = new YoutubeUrlValidator();
                validator.validate(url);
                UrlConverter converter = new YoutubeUrlConverter();
                List<Video> videos = null;
                try {
                    videos = converter.convert(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                AudioBuffer videoBuffer = new AudioBuffer();
                VideoFetcher youtubeVideoFetcher = new YoutubeVideoFetcher(videos.get(0).getUrl(), videoBuffer);
                Streamer audioStreamer = new AudioStreamer(videoBuffer, ws);
                try {
                    youtubeVideoFetcher.fetch();
                    audioStreamer.stream();
                } catch (InterruptedException e) {
                    log.error("Thread exepion.", e);
                } catch (IOException e) {
                    log.error("I/O exception.", e);
                }
            });
        }).listen(StreamerConst.WEBSOCKET_PORT);
    }
}
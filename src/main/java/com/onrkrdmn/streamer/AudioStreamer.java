package com.onrkrdmn.streamer;

import com.onrkrdmn.streamer.constant.StreamerConst;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * Audio streamer from url
 * Created by onur on 17.03.17.
 */
public class AudioStreamer extends AbstractVerticle{

    public void init(){
        Vertx.vertx().deployVerticle(AudioStreamer.class.getName());
    }

    @Override
    public void start() throws Exception {
        final Router router = Router.router(vertx);
        router.get("/stream").handler(ctx -> {
//            vertx.fileSystem().readDir()
        });
        router.route().handler(StaticHandler.create());

        // Web server
        vertx.createHttpServer().requestHandler(router::accept).listen(StreamerConst.HTPP_PORT);

        // mp3 stream web socket
        vertx.createHttpServer().websocketHandler(ws -> {

        });
    }
}

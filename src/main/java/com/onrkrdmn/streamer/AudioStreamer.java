package com.onrkrdmn.streamer;

import com.onrkrdmn.core.AudioBuffer;
import com.onrkrdmn.core.StaticApplicationContext;
import com.onrkrdmn.core.ThreadService;
import com.onrkrdmn.core.model.FrameGroup;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import lombok.extern.log4j.Log4j;

import java.io.ByteArrayOutputStream;

/**
 * Audio streamer through web socket via vertx.io
 * <p>
 * Created by onur on 17.03.17.
 */
@Log4j
public class AudioStreamer implements Streamer, Runnable {

    private String PROCESS_KEY = "audio.streamer";

    /**
     * AudioBuffer for async read and send operations
     */
    private AudioBuffer videoBuffer;

    /**
     * Websocket
     * TODO websocket should make general in order to use another method for data sending (like another library)
     */
    private ServerWebSocket webSocket;

    /**
     * ThreadManager instance
     */
    private ThreadService threadService;

    private boolean isContinue;

    public AudioStreamer(AudioBuffer videoBuffer, ServerWebSocket webSocket) {
        if (videoBuffer == null || webSocket == null) {
            throw new NullPointerException("AudioBuffer and WebSocket couldn't be null");
        }
        this.videoBuffer = videoBuffer;
        this.webSocket = webSocket;
        this.isContinue = true;
        this.threadService = (ThreadService) StaticApplicationContext.getBean("threadService");
    }

    @Override
    public void stream() throws InterruptedException {
        this.threadService.execute(this.PROCESS_KEY, this);
    }

    @Override
    public void run() {
        while (!videoBuffer.isDone() && this.isContinue) {

            try {
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Buffer buffer = Buffer.buffer();

                // collect 20 frame group and then send them as one array buffer
                for (int i = 0; i < 20; i++) {
                    FrameGroup taken = videoBuffer.take();
                    baos.write(taken.getBytes(), taken.getOffset(), taken.getLength());
                }
                buffer.appendInt(1);
                buffer.appendBytes(baos.toByteArray());
                webSocket.write(buffer);
            } catch (InterruptedException e) {
                log.error("Audio streaming exception", e);
            }
        }
    }

    public void stop() {
        this.isContinue = false;
    }


}

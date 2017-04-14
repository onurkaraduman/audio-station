package com.onrkrdmn.streamer;

/**
 * Streaming service
 *
 * @author Onur Karaduman
 * @since 02.04.17
 */
public interface Streamer {

    /**
     * Stream the data
     *
     * @throws InterruptedException
     */
    public void stream() throws InterruptedException;

    /**
     * Stop the streaming
     */
    public void stop();
}

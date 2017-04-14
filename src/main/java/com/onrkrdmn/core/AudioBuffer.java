package com.onrkrdmn.core;

import com.onrkrdmn.core.model.FrameGroup;
import lombok.extern.log4j.Log4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Video buffer with {@link BlockingQueue}
 * <p>
 * Created by onur on 30.03.17.
 */
@Log4j
public class AudioBuffer {

    private int CAPACITY = 4096;

    private BlockingQueue<FrameGroup> frameGroupBuffer = new ArrayBlockingQueue<FrameGroup>(this.CAPACITY);

    private boolean isDone = false;

    public synchronized void put(int index, byte[] bytes, int offset, int length) throws InterruptedException {
        if (this.frameGroupBuffer.size() >= this.CAPACITY) {
            log.info("Buffer is full. Thread is waiting");
            wait();
        }
        this.addFrameToBuffer(index, bytes, offset, length);
        notifyAll();
    }

    public synchronized void put(FrameGroup frameGroup) throws InterruptedException {
        if (this.frameGroupBuffer.size() >= this.CAPACITY) {
            log.info("Buffer is full. Thread is waiting");
            wait();
        }
        this.frameGroupBuffer.put(frameGroup);
        notifyAll();
    }

    public synchronized FrameGroup take() throws InterruptedException {
        if (this.frameGroupBuffer.size() <= 0) {
            log.info("Buffer is empty. Thread is waiting");
            wait();
        }
        FrameGroup frameGroup = this.frameGroupBuffer.take();
        notifyAll();
        return frameGroup;
    }

    public synchronized boolean isDone() {
        return isDone;
    }

    public synchronized void setDone(boolean done) throws InterruptedException {
        isDone = done;
        Thread.sleep(1000);
        notifyAll();
    }

    public int getQueueSize() {
        return this.frameGroupBuffer.size();
    }

    public boolean isEmpty() {
        return this.frameGroupBuffer.isEmpty();
    }

    private void addFrameToBuffer(int index, byte[] bytes, int offset, int length) throws InterruptedException {
        FrameGroup frameGroup = new FrameGroup(index, bytes, offset, length);
        this.frameGroupBuffer.put(frameGroup);
    }
}

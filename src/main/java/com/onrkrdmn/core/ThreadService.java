package com.onrkrdmn.core;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Thread manager
 *
 * @author Onur Karaduman
 * @since 02.04.17
 */
@Service
@Log4j
public class ThreadService {

    //TODO create control thread if the thread is alive in maps
    private Map<String, Thread> threads;

    @PostConstruct
    public void init() {
        threads = new HashMap<>();
    }

    public void execute(String key, Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setName(key);
        this.saveThread(key, thread);
        log.info("Thread is starting...... ID: " + key);
        thread.start();
    }

    public Thread getThread(String key) {
        return this.threads.get(key);
    }

    private void saveThread(String key, Thread thread) {
        this.threads.put(key, thread);
    }


}

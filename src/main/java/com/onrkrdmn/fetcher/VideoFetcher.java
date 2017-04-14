package com.onrkrdmn.fetcher;

import java.io.IOException;

/**
 * Created by onur on 29.03.17.
 */
public interface VideoFetcher {
    public void fetch() throws IOException, InterruptedException;
}

package com.onrkrdmn.vido2audio;

import com.xuggle.mediatool.*;
import com.xuggle.mediatool.event.IAddStreamEvent;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IStreamCoder;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Video to audio converter
 * Created by onur on 17.03.17.
 */
@Service
public class V2AConverter {

    public void convertToMP3(File input, File output, int kbps) { //modify on your convenience
        // create a media reader
        IMediaReader mediaReader = ToolFactory.makeReader(input.getPath());

        // create a media writer
        IMediaWriter mediaWriter = ToolFactory.makeWriter(output.getPath(), mediaReader);

        // add a writer to the reader, to create the output file
        mediaReader.addListener(mediaWriter);

//        IMediaViewer mediaViewer = ToolFactory.makeViewer(true);
//        mediaReader.addListener(mediaViewer);


//        // add a IMediaListner to the writer to change bit rate
//        mediaWriter.addListener(new MediaListenerAdapter() {
//            @Override
//            public void onAddStream(IAddStreamEvent event) {
//                IStreamCoder streamCoder = event.getSource().getContainer().getStream(event.getStreamIndex()).getStreamCoder();
//                streamCoder.setFlag(IStreamCoder.Flags.FLAG_QSCALE, false);
//                streamCoder.setBitRate(kbps);
//                streamCoder.setBitRateTolerance(0);
//            }
//        });

        // read and decode packets from the source file and
        // and dispatch decoded audio and video to the writer
        while (mediaReader.readPacket() == null) ;
    }
}

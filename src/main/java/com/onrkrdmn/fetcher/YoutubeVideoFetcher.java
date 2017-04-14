package com.onrkrdmn.fetcher;

import com.onrkrdmn.core.AudioBuffer;
import com.onrkrdmn.core.StaticApplicationContext;
import com.onrkrdmn.core.ThreadService;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.*;
import com.xuggle.xuggler.io.XugglerIO;
import lombok.extern.log4j.Log4j;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The class is charge of fetching data from youtube
 * and buffers in {@link AudioBuffer}
 * Created by onur on 29.03.17.
 */
@Log4j
public class YoutubeVideoFetcher implements VideoFetcher, Runnable {

    private String PROCESS_KEY = "youtube.fetcher";

    private final String url;

    private final AudioBuffer videoBuffer;

    private final ThreadService threadService;

    private boolean isDone = false;

    public YoutubeVideoFetcher(String url, AudioBuffer videoBuffer) {
        this.url = url;
        this.videoBuffer = videoBuffer;
        this.threadService = (ThreadService) StaticApplicationContext.getBean("threadService");
    }

    @Override
    public void fetch() throws IOException, InterruptedException {
        this.threadService.execute(this.PROCESS_KEY, this);
    }


    private void streamToSource(Path path) throws IOException, InterruptedException {

        byte[] buffer = new byte[4096];
        PipedInputStream pis = new PipedInputStream();
        PipedOutputStream pos = new PipedOutputStream(pis);
        convertToMP3Xuggler(path, pos);

        int nRead = 0;
        int index = 0;
        log.info(String.format("Fetching started. Url: %s ", this.url));
        while ((nRead = pis.read(buffer)) != -1) {
            this.videoBuffer.put(index++, buffer, 0, nRead);
        }
        this.videoBuffer.setDone(true);
        log.info("Fetching done.............");
        pis.close();
    }

    private void convertToMP3Xuggler(Path path, PipedOutputStream pos) throws FileNotFoundException {

        // create a media reader
        // final IMediaReader mediaReader = ToolFactory.makeReader( XugglerIO.map( new FileInputStream( path.toFile( ) ) ) );

        // create a media writer
        // IMediaWriter mediaWriter = ToolFactory.makeWriter( XugglerIO.map( XugglerIO.generateUniqueName( os, ".mp3" ), os ), mediaReader );
        IMediaWriter mediaWriter = ToolFactory.makeWriter(XugglerIO.map(pos));
        // manually set the container format (because it can't detect it by filename anymore)


        IContainerFormat containerFormat = IContainerFormat.make();
        containerFormat.setOutputFormat("mp3", null, "audio/mp3");
        mediaWriter.getContainer().setFormat(containerFormat);

        System.out.println("file = " + path.toFile().toString());

        IContainer audioContainer = IContainer.make();
        audioContainer.open(path.toFile().toString(), IContainer.Type.READ, null);

        System.out.println("streams= " + audioContainer.getNumStreams());
        System.out.println("# Duration (ms): " + ((audioContainer.getDuration() == Global.NO_PTS) ? "unknown" : "" + audioContainer.getDuration() / 1000));
        System.out.println("# File size (bytes): " + audioContainer.getFileSize());
        System.out.println("# Bit rate: " + audioContainer.getBitRate());
        int audioStreamId = -1;


        for (int i = 0; i < audioContainer.getNumStreams(); i++) {
            // Find the streamer object
            IStream stream = audioContainer.getStream(i);
            // Get the pre-configured decoder that can decode this streamer;
            IStreamCoder coder = stream.getStreamCoder();
            if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
                audioStreamId = i;
                break;
            }
        }
        if (audioStreamId < 0) {
            throw new IllegalArgumentException("cannot find audio streamer in the current file : " + path.toString());
        }
        System.out.println("found audio streamer = " + audioStreamId);

        IStreamCoder coderAudio = audioContainer.getStream(audioStreamId).getStreamCoder();

        if (coderAudio.open(null, null) < 0) {
            throw new RuntimeException("Cant open audio coder");
        }
        coderAudio.setSampleFormat(IAudioSamples.Format.FMT_S16);

        System.out.println("bitrate from reading = " + audioContainer.getBitRate());
        System.out.println("bitrate from reading = " + coderAudio.getBitRate());

        int streamIndex = mediaWriter.addAudioStream(0, 0, coderAudio.getChannels(), coderAudio.getSampleRate());
        IStreamCoder writerCoder = mediaWriter.getContainer().getStream(streamIndex).getStreamCoder();
        writerCoder.setFlag(IStreamCoder.Flags.FLAG_QSCALE, false);
        writerCoder.setBitRate(4200 * 1000);
        writerCoder.setBitRateTolerance(0);
        System.out.println("bitrate for output = " + writerCoder.getBitRate());

        IPacket packet = IPacket.make();

        runInThread(path, pos, mediaWriter, audioContainer, audioStreamId, coderAudio, streamIndex, packet);

    }

    private void runInThread(Path path, PipedOutputStream pos, IMediaWriter mediaWriter, IContainer audioContainer, int audioStreamId, IStreamCoder coderAudio, int streamIndex, IPacket packet) {

        new Thread() {
            @Override
            public void run() {
                int a = 0;
                while (audioContainer.readNextPacket(packet) >= 0) {

                /*
                 * Now we have a packet, let's see if it belongs to our audio streamer
                 */
                    if (packet.getStreamIndex() == audioStreamId) {
                    /*
                     * We allocate a set of samples with the same number of channels as the
                     * coder tells us is in this buffer.
                     * We also pass in a buffer size (4096 in our example), although Xuggler
                     * will probably allocate more space than just the 4096 (it's not important why).
                     */

                        IAudioSamples samples = IAudioSamples.make(4096, coderAudio.getChannels(), IAudioSamples.Format.FMT_S16);

                    /*
                     * A packet can actually contain multiple sets of samples (or frames of samples
                     * in audio-decoding speak). So, we may need to call decode audio multiple
                     * times at different offsets in the packet's data. We capture that here.
                     */
                        int offset = 0;

                    /*
                     * Keep going until we've processed all data
                     */

                        while (offset < packet.getSize()) {
                            int bytesDecoded = coderAudio.decodeAudio(samples, packet, offset);
                            if (bytesDecoded < 0) {
                                System.out.println("decode error in : " + path + " bytesDecoded =" + bytesDecoded + " offset=" + offset + " packet=" + packet);
                                break;
                                //                                throw new RuntimeException( "got error decoding audio in: " + path );
                            }

                            offset += bytesDecoded;

                            //                            System.out.println( "pktSize = " + packet.getSize( ) + "  offset = " + offset + " samplesComplete = " + samples.isComplete( ) );

                        /*
                         * Some decoder will consume data in a packet, but will not be able to construct
                         * a full set of samples yet. Therefore you should always check if you
                         * got a complete set of samples from the decoder
                         */
                            if (samples.isComplete()) {
                                mediaWriter.encodeAudio(streamIndex, samples);
                            }
                        }
                    }
                }
                coderAudio.close();
                audioContainer.close();
                mediaWriter.close();
                YoutubeVideoFetcher.this.isDone = true;
                try {
                    pos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    @Override
    public void run() {
        log.info("Fetch is starting........");
        Path path = Paths.get(url);
        try {
            streamToSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            log.error("Fetch exception", e);
        }
    }
}

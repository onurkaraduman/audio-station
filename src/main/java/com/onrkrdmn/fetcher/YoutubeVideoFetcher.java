package com.onrkrdmn.fetcher;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.*;
import com.xuggle.xuggler.io.XugglerIO;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by onur on 29.03.17.
 */
public class YoutubeVideoFetcher {

    private static final String inputFilename = "source.3gp";
    private static final String outputFilename = "dest.mp3";
    private static final String url = "https://r20---sn-4g57kn67.googlevideo.com/videoplayback?key=yt6&lmt=1490333138127650&dur=8.428&initcwndbps=3657500&ipbits=0&id=o-AIG_ywHnt-3YDCLjBbVxzJhpzKupASDaktqLOQhrVt8p&ratebypass=yes&ip=82.195.74.114&sparams=dur%2Cei%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Clmt%2Cmime%2Cmm%2Cmn%2Cms%2Cmv%2Cpl%2Cratebypass%2Crequiressl%2Csource%2Cupn%2Cexpire&mv=m&upn=di5Ue7PTj2A&pl=22&itag=22&mt=1490728472&ms=au&ei=m7baWJ2-C5eQWvOVgoAF&mn=sn-4g57kn67&mm=31&mime=video%2Fmp4&expire=1490750203&requiressl=yes&signature=D7793924CEF3597DFA0C0D2EC393BA1CFE899858.15B70638BC73F49A074C59A645599B3423BC3035&source=youtube&signature=D7793924CEF3597DFA0C0D2EC393BA1CFE899858.15B70638BC73F49A074C59A645599B3423BC3035";

    public void fetcher() throws IOException {

        Path p = Paths.get(url);
        OutputStream os = new FileOutputStream(outputFilename);
        streamToSource(os, p);
    }


    private void streamToSource(OutputStream source, Path path) throws IOException {

        byte[] buffer = new byte[4096];
        PipedInputStream pis = new PipedInputStream();
        PipedOutputStream pos = new PipedOutputStream(pis);
        convertToMP3Xuggler(path, pos);

        System.out.println("start streaming");
        int nRead = 0;
        while ((nRead = pis.read(buffer)) != -1) {
            source.write(buffer, 0, nRead);
        }
        pis.close();

        System.out.println("end : " + path);

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
            // Find the stream object
            IStream stream = audioContainer.getStream(i);
            // Get the pre-configured decoder that can decode this stream;
            IStreamCoder coder = stream.getStreamCoder();
            if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
                audioStreamId = i;
                break;
            }
        }
        if (audioStreamId < 0) {
            throw new IllegalArgumentException("cannot find audio stream in the current file : " + path.toString());
        }
        System.out.println("found audio stream = " + audioStreamId);

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
                    System.out.println("debug::: " + a++);
                /*
                 * Now we have a packet, let's see if it belongs to our audio stream
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
                try {
                    pos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

}

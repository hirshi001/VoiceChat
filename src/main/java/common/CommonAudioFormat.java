package common;

import javax.sound.sampled.AudioFormat;

public class CommonAudioFormat {

    public static final int PACKET_SIZE = 256;
    public static AudioFormat getAudioFormat() {
        float sampleRate = 8000.0F;
        // You can try also 8000,11025,16000,22050,44100
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,
                bigEndian);
    }
}

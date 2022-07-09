package client;

import com.hirshi001.networking.network.client.Client;
import common.CommonAudioFormat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Arrays;
import java.util.function.Consumer;

import javax.sound.sampled.*;

// Class for capturing and saving into file, audio from mic




public class AudioCapture {

    boolean stopCapture = false;
    ByteArrayOutputStream byteArrayOutputStream;
    AudioFormat audioFormat;
    TargetDataLine targetDataLine;
    AudioInputStream audioInputStream;
    SourceDataLine sourceDataLine;
    public Consumer<byte[]> audioCaptureCallback;


    FileOutputStream fout;
    AudioFileFormat.Type fileType;


    // Captures audio input
// from mic.
// Saves input in
// a ByteArrayOutputStream.
    public void captureAudio() {
        try {

            audioFormat = CommonAudioFormat.getAudioFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(
                    TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();


            // Thread to capture from mic
            // This thread will run till stopCapture variable
            // becomes true. This will happen when saveAudio()
            // method is called.
            Thread captureThread = new Thread(new CaptureThread());
            captureThread.start();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    // Saves the data from
// ByteArrayOutputStream
// into a file
    public void saveAudio(File filename) {
        stopCapture = true;
        try {

            byte audioData[] = byteArrayOutputStream.toByteArray();

            InputStream byteArrayInputStream = new ByteArrayInputStream(
                    audioData);
            AudioFormat audioFormat = CommonAudioFormat.getAudioFormat();
            audioInputStream = new AudioInputStream(byteArrayInputStream,
                    audioFormat, audioData.length / audioFormat.getFrameSize());
            DataLine.Info dataLineInfo = new DataLine.Info(
                    SourceDataLine.class, audioFormat);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();

            // This thread will actually do the job
            Thread saveThread = new Thread(new SaveThread(filename));
            saveThread.start();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }



    class CaptureThread extends Thread {
        // temporary buffer
        byte tempBuffer[] = new byte[CommonAudioFormat.PACKET_SIZE];

        public void run() {
            byteArrayOutputStream = new ByteArrayOutputStream();
            stopCapture = false;
            try {
                while (!stopCapture) {

                    int cnt = targetDataLine.read(tempBuffer, 0,
                            tempBuffer.length);
                    if(audioCaptureCallback != null) {
                        audioCaptureCallback.accept(tempBuffer);
                    }
                    if (cnt > 0) {
                        byteArrayOutputStream.write(tempBuffer, 0, cnt);
                    }
                }
                byteArrayOutputStream.close();
            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }

    class SaveThread extends Thread {
        // Set a file from saving from ByteArrayOutputStream
        File fname;

        public SaveThread(File fname) {
            this.fname = fname;
        }

        //
        byte tempBuffer[] = new byte[10000];

        public void run() {
            try {
                int cnt;

                if (AudioSystem.isFileTypeSupported(AudioFileFormat.Type.AU,
                        audioInputStream)) {
                    AudioSystem.write(audioInputStream,
                            AudioFileFormat.Type.AU, fname);
                }

            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }

}

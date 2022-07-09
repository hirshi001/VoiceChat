package server;

import com.hirshi001.networking.network.server.Server;
import com.hirshi001.networking.networkdata.DefaultNetworkData;
import com.hirshi001.networking.networkdata.NetworkData;
import com.hirshi001.networking.packethandlercontext.PacketHandlerContext;
import com.hirshi001.networking.packetregistrycontainer.PacketRegistryContainer;
import com.hirshi001.networking.packetregistrycontainer.SinglePacketRegistryContainer;
import common.Common;
import common.CommonAudioFormat;
import common.VoicePacket;

import javax.sound.sampled.*;
import javax.swing.*;

public class VoiceServer {

    static AudioInputStream ais;
    static AudioFormat format;
    static boolean status = true;
    static int sampleRate = 44100;

    static DataLine.Info dataLineInfo;
    static SourceDataLine sourceDataLine;

    public static void main(String args[]) throws Exception
    {

        JFrame frame = new JFrame("Voice Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(100, 100);
        frame.setVisible(true);

        PacketRegistryContainer packetRegistryContainer = new SinglePacketRegistryContainer();

        packetRegistryContainer.getDefaultRegistry()
                .registerDefaultPrimitivePackets()
                .registerDefaultArrayPrimitivePackets()
                .register(VoicePacket::new, VoiceServer::VoicePacketHandle, VoicePacket.class, 1);

        NetworkData networkData = new DefaultNetworkData(Common.packetEncoderDecoder, packetRegistryContainer);

        Server server = Common.networkFactory.createServer(networkData, Common.bufferFactory, Common.port);

        server.startUDP().perform().get();
        System.out.println("Server started");
        /**
         * Formula for lag = (byte_size/sample_rate)*2
         * Byte size 9728 will produce ~ 0.45 seconds of lag. Voice slightly broken.
         * Byte size 1400 will produce ~ 0.06 seconds of lag. Voice extremely broken.
         * Byte size 4000 will produce ~ 0.18 seconds of lag. Voice slightly more broken then 9728.
         */

        format = CommonAudioFormat.getAudioFormat();

        dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
        sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
        sourceDataLine.open(format);
        sourceDataLine.start();

    }

    public static void VoicePacketHandle(PacketHandlerContext<VoicePacket> voicePacket)
    {
        //ais = new AudioInputStream(new ByteArrayInputStream(VoicePacket.data, 0, VoicePacket.data.length), format, VoicePacket.data.length);
        toSpeaker(VoicePacket.data);
    }

    public static void toSpeaker(byte soundbytes[]) {
        try
        {
            sourceDataLine.write(soundbytes, 0, soundbytes.length);
        } catch (Exception e) {
            System.out.println("Not working in speakers...");
            e.printStackTrace();
        }
    }
}
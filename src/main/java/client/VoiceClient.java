package client;

import com.hirshi001.networking.network.client.Client;
import com.hirshi001.networking.networkdata.DefaultNetworkData;
import com.hirshi001.networking.networkdata.NetworkData;
import com.hirshi001.networking.packetregistrycontainer.PacketRegistryContainer;
import com.hirshi001.networking.packetregistrycontainer.SinglePacketRegistryContainer;
import common.Common;
import common.VoicePacket;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class VoiceClient {

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {


        JFrame frame = new JFrame("Voice Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(100, 100);
        frame.setVisible(true);


        AudioCapture audioCapture = new AudioCapture();

        PacketRegistryContainer packetRegistryContainer = new SinglePacketRegistryContainer();

        packetRegistryContainer.getDefaultRegistry()
                .registerDefaultPrimitivePackets()
                .registerDefaultArrayPrimitivePackets()
                .register(VoicePacket::new, null, VoicePacket.class, 1);

        NetworkData networkData = new DefaultNetworkData(Common.packetEncoderDecoder, packetRegistryContainer);

        Client client = Common.networkFactory.createClient(networkData, Common.bufferFactory, "2601:646:8481:ef30:9096:f797:7196:f934", Common.port);

        client.startUDP().perform().get();


        audioCapture.audioCaptureCallback = (byte[] data) -> {
            try {
                client.sendUDP(new VoicePacket(data), null).perform().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        };


        audioCapture.captureAudio();
        audioCapture.stopCapture = true;
    }

}

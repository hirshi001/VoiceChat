package common;

import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.networking.packet.Packet;

import java.util.concurrent.ThreadLocalRandom;

public class VoicePacket extends Packet {


    public static byte[] data = new byte[CommonAudioFormat.PACKET_SIZE];

    public VoicePacket(){

    }

    public VoicePacket(byte[] data) {
        super();
        System.arraycopy(data, 0, this.data, 0, data.length);
    }

    @Override
    public void writeBytes(ByteBuffer out) {
        super.writeBytes(out);
        out.ensureWritable(data.length + 4);
        out.writeInt(data.length);
        out.writeBytes(data);
    }

    @Override
    public void readBytes(ByteBuffer in) {
        super.readBytes(in);
        int size = in.readInt();
        in.readBytes(data, 0, size);
    }
}

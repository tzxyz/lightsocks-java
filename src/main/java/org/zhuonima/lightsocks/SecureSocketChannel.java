package org.zhuonima.lightsocks;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SecureSocketChannel {

    private final Cipher cipher;
    private final SocketChannel channel;

    private final int bufferSize = 1024;

    public SecureSocketChannel(Cipher cipher, SocketChannel channel) {
        this.cipher = cipher;
        this.channel = channel;
    }

    public int encodeAndWrite(byte[] data) {

        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

        cipher.encode(data);

        buffer.put(data);

        buffer.flip();

        return buffer.limit();
    }

    public int decodeAndRead(byte[] data) throws IOException {

        ByteBuffer buffer = ByteBuffer.wrap(data);

        int n = channel.read(buffer);

        cipher.decode(data);

        return 0;
    }
}

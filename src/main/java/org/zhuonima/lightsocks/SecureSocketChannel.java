package org.zhuonima.lightsocks;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class SecureSocketChannel {

    private final Cipher cipher;
    private final InetSocketAddress remote;

    private final int bufferSize = 1024 * 4;

    public SecureSocketChannel(Cipher cipher, InetSocketAddress remote) {
        this.cipher = cipher;
        this.remote = remote;
    }

    public int encodeAndWrite(SocketChannel channel, byte[] data) throws IOException {

        cipher.encode(data);

        ByteBuffer buffer = ByteBuffer.wrap(data);

        buffer.flip();

        return channel.write(buffer);
    }

    public int decodeAndRead(SocketChannel channel, byte[] data) throws IOException {

        ByteBuffer buffer = ByteBuffer.wrap(data);

        int n = channel.read(buffer);

        cipher.decode(data);

        return n;
    }

    public void encodeCopy(SocketChannel src, SocketChannel dst) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        while (true) {
            int n = src.read(buffer);
            if (n > 0) {
                buffer.flip();
                this.encodeAndWrite(dst, Arrays.copyOf(buffer.array(), n));
            } else return;
        }
    }

    public void decodeCopy(SocketChannel src, SocketChannel dst) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        while (true) {
            int n = this.decodeAndRead(src, buffer.array());
            if (n > 0) {
                buffer.flip();
                dst.write(buffer);
            } else return;
        }
    }

    public SocketChannel dialRemote() throws IOException {
        return SocketChannel.open(remote);
    }

}

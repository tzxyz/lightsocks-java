package org.zhuonima.lightsocks;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientTest {

    @Test
    public void test() throws Exception {
//        SocketChannel channel = SocketChannel.open(new InetSocketAddress(9999));
//        channel.write(ByteBuffer.wrap(new byte[]{1, 2, 3}));
//        channel.close();
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("localhost", 9999));
        socket.getOutputStream().write(new byte[]{1, 2, 3});
        Thread.sleep(10000);
        socket.close();
    }
}

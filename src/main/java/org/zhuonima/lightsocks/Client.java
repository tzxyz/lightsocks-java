package org.zhuonima.lightsocks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;

public class Client {

    private final InetSocketAddress local;

    private final InetSocketAddress remote;

    private final ServerSocketChannel serverSocketChannel;

    private final Selector selector = Selector.open();

    private final Logger logger = LoggerFactory.getLogger(Client.class);

    public Client(InetSocketAddress local, InetSocketAddress remote) throws IOException {
        this.local = local;
        this.remote = remote;
        this.serverSocketChannel = ServerSocketChannel.open();
    }

    public void listen() throws IOException {
        this.serverSocketChannel.bind(local);
        this.serverSocketChannel.configureBlocking(false);
        this.serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        logger.info("Bind local address: {}:{}", local.getHostString(), local.getPort());
        handle();
    }

    private void handle() throws IOException {

        while (true) {
            int n = selector.selectNow();

            if (n < 0) continue;


            for (Iterator<SelectionKey> iterator = selector.selectedKeys().iterator(); iterator.hasNext(); ) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isConnectable()) {
                    SocketChannel sc = (SocketChannel) key.channel();
                    sc.finishConnect();
                    logger.debug("connect");
                } else if (key.isAcceptable()) {
                    logger.info("acc");
                    SocketChannel sc = serverSocketChannel.accept();
                    if (sc == null) continue;
                    sc.configureBlocking(false);
                    sc.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    SocketChannel sc = (SocketChannel) key.channel();
                    sc.configureBlocking(false);

                    test(sc);
                    // 读取src， 加密，写入remote
//                    SocketChannel remote = SocketChannel.open(this.remote);
//
//                    remote.configureBlocking(false);
//                    SelectionKey writeKey = remote.register(selector, SelectionKey.OP_WRITE);
//                    writeKey.attach(ByteBuffer.allocate(1024));

                } else if (key.isWritable()) {
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    SocketChannel sc = (SocketChannel) key.channel();
                    sc.write(buffer);
                }
            }
        }

    }

    private void test(SocketChannel sc) throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(4096);

        sc.read(buffer);

        buffer.flip();

        int n = buffer.limit();

        byte data[] = new byte[n];

        buffer.get(data);

        System.out.println(Arrays.toString(data));

    }
}

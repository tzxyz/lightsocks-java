package org.zhuonima.lightsocks;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Client {

    private final InetSocketAddress local;

    private final InetSocketAddress remote;

    private final ServerSocketChannel serverSocketChannel;

    private final Selector selector = Selector.open();

    public Client(InetSocketAddress local, InetSocketAddress remote) throws IOException {
        this.local = local;
        this.remote = remote;
        this.serverSocketChannel = ServerSocketChannel.open();
    }

    public void listen() throws IOException {
        this.serverSocketChannel.bind(local);
        this.serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void handle() throws IOException {

        while (true) {
            int n = selector.selectNow();

            if (n < 0) continue;

            Set<SelectionKey> keys = selector.selectedKeys();

            Iterator<SelectionKey> iterator = keys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();

                if (key.isAcceptable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    socketChannel.configureBlocking(false);
                } else if (key.isWritable()) {

                }
            }
        }

    }
}

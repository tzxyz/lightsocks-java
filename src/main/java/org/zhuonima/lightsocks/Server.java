package org.zhuonima.lightsocks;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server {

    private final InetSocketAddress listen;

    private final ServerSocketChannel server = ServerSocketChannel.open();

    private final Selector selector = Selector.open();

    public Server(InetSocketAddress listen) throws IOException {
        this.listen = listen;
        this.server.bind(listen);
        this.server.configureBlocking(false);
        this.server.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void serve() throws IOException {
        for (;;) {
            for (Iterator<SelectionKey> iterator = selector.selectedKeys().iterator(); iterator.hasNext();) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    SocketChannel sc = server.accept();
                    sc.configureBlocking(false);
                    sc.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {

                    SocketChannel sc = (SocketChannel) key.channel();

                    sc.configureBlocking(false);

                    handle(sc);

                    sc.register(selector, SelectionKey.OP_WRITE);

                } else if (key.isWritable()) {



                }
            }
        }
    }

    private void handle(SocketChannel sc) {

    }
}

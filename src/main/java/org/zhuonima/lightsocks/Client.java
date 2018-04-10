package org.zhuonima.lightsocks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Client {

    private final InetSocketAddress local;

    private final InetSocketAddress remote;

    private final ServerSocketChannel serverSocketChannel;

    private final Selector selector = Selector.open();

    private final Cipher cipher;

    private final Logger logger = LoggerFactory.getLogger(Client.class);

    public Client(String password, InetSocketAddress local, InetSocketAddress remote) throws IOException {
        this.local = local;
        this.remote = remote;
        this.serverSocketChannel = ServerSocketChannel.open();
        this.cipher = new Cipher(PasswordFactory.parse(password));
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
            int n = selector.select();

            if (n < 0) continue;


            for (Iterator<SelectionKey> iterator = selector.selectedKeys().iterator(); iterator.hasNext(); ) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isConnectable()) {
                    SocketChannel sc = (SocketChannel) key.channel();
                    sc.finishConnect();
                } else if (key.isAcceptable()) {
                    SocketChannel sc = serverSocketChannel.accept();
                    if (sc == null) continue;
                    sc.configureBlocking(false);
                    sc.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    SocketChannel sc = (SocketChannel) key.channel();
                    sc.configureBlocking(false);
                    SecureSocketChannel secureSocketChannel = new SecureSocketChannel(cipher);
                    SocketChannel remote = secureSocketChannel.dialRemote(this.remote);
                    remote.configureBlocking(false);
                    secureSocketChannel.encodeCopy(sc, remote);
                    SelectionKey writableKey = remote.register(selector, SelectionKey.OP_WRITE);
                    writableKey.attach(sc);
                } else if (key.isWritable()) {
                    SocketChannel remote = (SocketChannel) key.channel();
                    SocketChannel sc = (SocketChannel) key.attachment();
                    SecureSocketChannel secureSocketChannel = new SecureSocketChannel(cipher);
                    secureSocketChannel.decodeCopy(remote, sc);
                }
            }
        }

    }
}

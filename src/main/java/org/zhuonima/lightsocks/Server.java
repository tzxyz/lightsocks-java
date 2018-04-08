package org.zhuonima.lightsocks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server {

    private final InetSocketAddress listen;

    private final ServerSocketChannel server = ServerSocketChannel.open();

    private final Selector selector = Selector.open();

    private final Password password = PasswordFactory.newPassword();

    private final Cipher cipher;

    private final Logger logger = LoggerFactory.getLogger(Server.class);

    public Server(InetSocketAddress listen) throws IOException {
        this.listen = listen;
        this.cipher = new Cipher(password);
        this.server.bind(listen);
        this.server.configureBlocking(false);
        this.server.register(selector, SelectionKey.OP_ACCEPT);
        logger.info("Server listen at: {}:{}, using password: {}", listen.getHostString(), listen.getPort(), password.toString());
    }

    public void serve() throws IOException {
        for (; ; ) {
            for (Iterator<SelectionKey> iterator = selector.selectedKeys().iterator(); iterator.hasNext(); ) {
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

    private void handle(SocketChannel sc) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(256);
        byte[] data = buffer.array();
        SecureSocketChannel secureSocketChannel = new SecureSocketChannel(cipher);
        secureSocketChannel.decodeAndRead(sc, data);

        /**
         The localConn connects to the dstServer, and sends a ver
         identifier/method selection message:
         +----+----------+----------+
         |VER | NMETHODS | METHODS  |
         +----+----------+----------+
         | 1  |    1     | 1 to 255 |
         +----+----------+----------+
         The VER field is set to X'05' for this ver of the protocol.  The
         NMETHODS field contains the number of method identifier octets that
         appear in the METHODS field.
         */
        // 第一个字段VER代表Socks的版本，Socks5默认为0x05，其固定长度为1个字节
        if (data[0] != 0x05) return;

        /**
         The dstServer selects from one of the methods given in METHODS, and
         sends a METHOD selection message:
         +----+--------+
         |VER | METHOD |
         +----+--------+
         | 1  |   1    |
         +----+--------+
         */
        // 不需要验证，直接验证通过
        secureSocketChannel.encodeAndWrite(sc, new byte[]{0x05, 0x00});

        /**
         +----+-----+-------+------+----------+----------+
         |VER | CMD |  RSV  | ATYP | DST.ADDR | DST.PORT |
         +----+-----+-------+------+----------+----------+
         | 1  |  1  | X'00' |  1   | Variable |    2     |
         +----+-----+-------+------+----------+----------+
         */

        int n = secureSocketChannel.decodeAndRead(sc, data);

        if (n < 7) return;

        // CMD代表客户端请求的类型，值长度也是1个字节，有三种类型
        // CONNECT X'01'
        if (data[1] != 0x01) return;

        byte[] ip;
        byte[] port = new byte[2];

        switch (data[3]) {
            case 0x01:
                // ipv4 0x01
                ip = new byte[4];
                System.arraycopy(data, 3, ip, 0, 4);
                break;
            case 0x03:
                // domain
                byte domain[] = new byte[n - 7];
                System.arraycopy(data, 4, domain, 0, n - 2);
                InetAddress address = InetAddress.getByName(new String(domain));
                ip = address.getAddress();
                break;
            case 0x04:
                ip = new byte[16];
                System.arraycopy(data, 3, ip, 0, 16);
                break;
            default:
                return;
        }

        System.arraycopy(data, n - 3, port, 0, 2);

        int p = ByteBuffer.wrap(port).order(ByteOrder.BIG_ENDIAN).getInt();

        InetAddress address = InetAddress.getByAddress(ip);

        // 目标地址
        SocketAddress dst = new InetSocketAddress(address, p);

        // 连接真正的远程服务
        SocketChannel channel = SocketChannel.open(dst);

        secureSocketChannel.encodeAndWrite(sc, new byte[]{0x05, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});

        Thread t = new Thread(() -> {
            try {
                secureSocketChannel.decodeCopy(sc, channel);
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    sc.close();
                    channel.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        t.start();

        secureSocketChannel.encodeCopy(channel, sc);
    }
}

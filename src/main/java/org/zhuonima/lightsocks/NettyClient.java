package org.zhuonima.lightsocks;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.logging.LoggingHandler;

import java.io.IOException;
import java.util.List;

public class NettyClient {

    private final EventLoopGroup group = new NioEventLoopGroup();

    private final EventLoopGroup boss = new NioEventLoopGroup();

    private final EventLoopGroup worker = new NioEventLoopGroup();

    private ServerBootstrap serverBootstrap;

    private Bootstrap bootstrap;

    private final LoggingHandler handler = new LoggingHandler();

    private final int port;

    private final String s;


    public NettyClient(int port, String password) {
        this.port = port;
        this.s = password;

    }

    public void listen() throws IOException, InterruptedException {

        Password password = PasswordFactory.parse(this.s);

        serverBootstrap
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .handler(handler)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new CipherHandler(new Cipher(password)));
                    }
                })
                .bind(port)
                .sync();

    }

    @ChannelHandler.Sharable
    public static class CipherHandler extends ByteToMessageCodec<ByteBuf> {

        private final Cipher cipher;

        public CipherHandler(Cipher cipher) {
            this.cipher = cipher;
        }

        @Override
        protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
            int n = msg.readableBytes();
            byte data[] = new byte[n];
            msg.readBytes(data);
            cipher.encode(data);
            out.writeBytes(data);
        }

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            int n = in.readableBytes();
            byte data[] = new byte[n];
            in.readBytes(data);
            cipher.encode(data);
            ByteBuf buf = Unpooled.buffer(n);
            buf.writeBytes(data);
            out.add(data);
        }
    }
}

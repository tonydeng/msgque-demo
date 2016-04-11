package com.github.chord1645.msgque.demo.client;

import com.github.chord1645.msgque.demo.PaintData;
import com.github.chord1645.msgque.demo.client.handle.PaintClientHandler;
import com.github.chord1645.msgque.demo.codec.MsgpackDecoder;
import com.github.chord1645.msgque.demo.codec.MsgpackEncoder;
import com.github.chord1645.msgque.demo.ui.PaintFrame;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class PaintClient {
    public static void main(String[] args) throws Exception {
        new PaintClient().connect(9093, "127.0.0.1");
    }

    PaintFrame paintFrame;

    public PaintClient() {
        paintFrame = new PaintFrame("画图程序", this);
    }

    public ChannelFuture serverFuture;

    public void connect(int port, String host) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
                            ch.pipeline().addLast(new MsgpackDecoder());
                            ch.pipeline().addLast(new LengthFieldPrepender(2));
                            ch.pipeline().addLast(new MsgpackEncoder());
                            ch.pipeline().addLast(new PaintClientHandler(paintFrame));
                        }
                    });

            // Start the client.
            serverFuture = b.connect(host, port).sync(); // (5)
            serverFuture.channel().closeFuture().sync();
            System.currentTimeMillis();
        } finally {
            group.shutdownGracefully();
        }
    }

    public void write(PaintData paintData) {
        serverFuture.channel().writeAndFlush(paintData);
    }
}
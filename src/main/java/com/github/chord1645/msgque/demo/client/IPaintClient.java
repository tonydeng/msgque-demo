package com.github.chord1645.msgque.demo.client;

import com.github.chord1645.msgque.demo.PaintData;
import com.github.chord1645.msgque.demo.client.handle.PaintClientHandler;
import com.github.chord1645.msgque.demo.codec.MsgpackDecoder;
import com.github.chord1645.msgque.demo.codec.MsgpackEncoder;
import com.github.chord1645.msgque.demo.ui.Apoint;
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
import org.eclipse.paho.client.mqttv3.MqttException;

public interface IPaintClient {

    void clearCache();

    void flushCache()   ;

    void append(Apoint apoint);
}
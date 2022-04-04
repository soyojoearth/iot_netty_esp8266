package com.soyojo.netty_iot_demo.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author soyojo.earth@gmail.com
 * @address Shenzhen, China
 */
public class SimpleServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("framer",new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast("decoder",new StringDecoder());
        pipeline.addLast("encoder",new StringEncoder());

        //pipeline.addLast("decoder",new iotProtocolDecoder());
        //pipeline.addLast("encoder",new iotProtocolEncoder());

        //不要自己编写什么特别协议解析了，还不如直接用字符串，按json解析，方便多了!

        pipeline.addLast("handle",new SimpleServerHandler());

        System.out.println("Client"+ch.remoteAddress()+"连上服务器");

    }

}

package com.soyojo.netty_iot_demo.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author soyojo.earth@gmail.com
 * @address Shenzhen, China
 */
public class NettyServer {

    private int port;

    public NettyServer(int port){
        this.port = port;
        try {
            this.run();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public void run() throws Exception{
        //NioEventLoopGroup用来处理io操作的多线程事件循环器
        //bossGroup 用来处理接收进来的连接
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        //用来处理已经被接收的连接
        NioEventLoopGroup  workerGroup = new NioEventLoopGroup();

        try{
            //启动nio服务器的辅助启动类
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new SimpleServerInitializer())
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            System.out.println("NettyServer 启动了");

            //绑定接口，开始接收进来的连接
            ChannelFuture future = serverBootstrap.bind(this.port).sync();
            //等待服务器socket关闭
            future.channel().closeFuture().sync();

        }finally{
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            System.out.println("NettyServer关闭了");
        }

    }
}

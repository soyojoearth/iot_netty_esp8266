package com.soyojo.netty_iot_demo.netty;

import com.soyojo.netty_iot_demo.acl.IotAcl;
import com.soyojo.netty_iot_demo.bean.IotDevice;
import com.soyojo.netty_iot_demo.bean.IotMessageEntity;
import com.google.gson.Gson;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author soyojo.earth@gmail.com
 * @address Shenzhen, China
 */
public class SimpleServerHandler extends SimpleChannelInboundHandler<String> {

    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static Map<String, Channel> channelMap = new HashMap<>();

    public static Map<String, IotDevice> deviceMap = new HashMap<>();

    public static Map<String, Boolean> activeMap = new HashMap<>();

    //为了演示方便，deviceId和socket地址表就放在这里。通常应该放在数据库里面，和设备上下线状态等放在一起。
    public static Map<String, String> deviceSocketForSendMessage = new HashMap<>();
    //为了演示方便，deviceStatus放在这里。通常应该放在数据库里面。
    public static Map<String, String> deviceStatusMap = new HashMap<>();


    /**
     * 每当服务器端收到新的客户端连接时，存放客户端的channel存放
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception{

        //获取连接的channel，放入channels
        Channel incomeing = ctx.channel();
        channels.add(ctx.channel());

        //把socket地址当作key，把Channel放入Map
        channelMap.put(incomeing.remoteAddress().toString(),incomeing);

        //然后把本机ID、socket地址、设备id，更新到数据库（此处省略，请自行完善）
        //......

    }

    /**
     * 每当服务器端断开客户端连接时，移除客户端的channel
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx)  throws Exception{

        //获取连接的channel，从channels移除
        Channel incoming  = ctx.channel();
        channels.remove(ctx.channel());
        //从Map移除
        channelMap.remove(incoming.remoteAddress().toString());
        deviceMap.remove(incoming.remoteAddress().toString());
        activeMap.remove(incoming.remoteAddress().toString());

        //更新数据库，设备已下线（此处省略，请自行完善）
        //......

    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) {
        System.out.println(s);

        Channel channel  = channelHandlerContext.channel();


        if (s.equals("ping")){
            //处理心跳（不需要验证身份就可以维持心跳）
            channel.writeAndFlush("pong\n");
            System.out.println("ping: "+channel.remoteAddress());
        }
        else {
            //当作Json解析数据，更新数据
            Gson gson = new Gson();
            IotMessageEntity iotMessageEntity = gson.fromJson(s, IotMessageEntity.class);
            if (iotMessageEntity.type.equals("login"))
            {

                IotDevice iotDevice = new IotDevice();
                iotDevice.productKey = iotMessageEntity.productKey;
                iotDevice.deviceId = iotMessageEntity.deviceId;
                iotDevice.deviceSecret = iotMessageEntity.deviceSecret;

                //校验设备合法性
                //....
                if(new IotAcl().verifyDevice(iotDevice))
                {
                    //校验通过后，更新deviceMap
                    deviceMap.put(channel.remoteAddress().toString(),iotDevice);
                    deviceSocketForSendMessage.put(iotDevice.deviceId,channel.remoteAddress().toString());
                    //告知已通过验证
                    channel.writeAndFlush("loginSuccess\n");
                    System.out.println("loginSuccess");
                }
                else {
                    //校验没通过
                    channel.writeAndFlush("loginFail\n");
                    System.out.println("loginFail");
                }

            }
            else if (iotMessageEntity.type.equals("update")){
                if (deviceMap.containsKey(channel.remoteAddress().toString())) {
                    IotDevice iotDevice = deviceMap.get(channel.remoteAddress().toString());

                    //更新其它传感器数据等，或者发到消息队列，让别的服务器处理（此处省略，自行按需编写）
                    //...


                    System.out.println("update dp");
                    deviceStatusMap.put(iotDevice.deviceId,s);

                }
                else {
                    //未登录，则禁止访问
                    channel.writeAndFlush("AccessDenied\n");
                    //channel.close();
                    System.out.println("AccessDenied");
                }
            }

        }

    }


    /**
     * 服务器接收到客户端上线
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel incoming = ctx.channel();
        activeMap.put(incoming.remoteAddress().toString(),true);
        System.out.println("Client:"+incoming.remoteAddress()+"在线");
        //询问认证
        ctx.channel().writeAndFlush("who\n");
        System.out.println("Client:" + ctx.channel().remoteAddress()+" who active");
    }


    /**
     * 服务端监听到客户端不活动
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Channel incoming = ctx.channel();
        activeMap.remove(incoming.remoteAddress().toString());
        System.out.println("Client:" + incoming.remoteAddress()+"掉线");
    }


    /**
     * 当服务器端的io抛出异常时被调用
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) {
        Channel incoming = ctx.channel();
        System.out.println("Client"+incoming.remoteAddress()+"异常");
        //异常关闭连接
        cause.printStackTrace();

        channelMap.remove(incoming.remoteAddress().toString());
        deviceMap.remove(incoming.remoteAddress().toString());
        activeMap.remove(incoming.remoteAddress().toString());
        //更新数据库，异常（此处省略，自行按需编写）
        //...

        ctx.close();

    }

}
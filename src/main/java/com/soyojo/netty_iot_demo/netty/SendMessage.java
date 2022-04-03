package com.soyojo.netty_iot_demo.netty;

import com.soyojo.netty_iot_demo.bean.IotDevice;
import io.netty.channel.Channel;

import java.util.Map;

/**
 * @author soyojo.earth@gmail.com
 * @address Shenzhen, China
 */
public class SendMessage {

    public static void sendMessageToDevice(IotDevice iotDevice, String json){
        Map<String, String> deviceSocketForSendMessage = SimpleServerHandler.deviceSocketForSendMessage;
        if (deviceSocketForSendMessage.containsKey(iotDevice.deviceId)){
            String socketAddr = deviceSocketForSendMessage.get(iotDevice.deviceId);
            Map<String, Channel> channelMap = SimpleServerHandler.channelMap;
            if (channelMap.containsKey(socketAddr))
            {
                Channel channel = channelMap.get(socketAddr);
                channel.write(json);//发一段消息，通常是json，
                channel.writeAndFlush("\n");//最后要换行符结束，设备端读取时靠\n符号识别结束
            }
        }
    }

}

package com.soyojo.netty_iot_demo.controller.web;


import com.soyojo.netty_iot_demo.bean.IotDevice;
import com.soyojo.netty_iot_demo.bean.IotDeviceStatusProductA;
import com.soyojo.netty_iot_demo.netty.SendMessage;
import com.soyojo.netty_iot_demo.netty.SimpleServerHandler;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * @author soyojo.earth@gmail.com
 * @address Shenzhen, China
 */
@RestController
public class SetLedController {

    @RequestMapping(value = "/set/led")
    public ModelAndView index(
            ModelAndView model,
            @RequestParam(value="power", required=false) String power,
            @RequestParam(value="productKey", required=false) String productKey,
            @RequestParam(value="deviceId", required=false) String deviceId,
            @RequestParam(value="deviceSecret", required=false) String deviceSecret
    ) {

        model.setViewName("set_led");

        Map<String,Object> map = new HashMap<>();
        map.put("productKey",productKey);
        map.put("deviceId",deviceId);
        map.put("deviceSecret",deviceSecret);

        //为了演示方便，需要让页面自动填上这些设备三元数据
        model.addObject("productKey","a1a7Azal8hY");
        model.addObject("deviceId","d9006bc4cdfbe96");
        model.addObject("deviceSecret","d9006b9cfdf3a27f84c4cdfbe96aad01");

        if (power == null){
            model.addObject("power","OFF");
        }

        Map<String, String> deviceStatusMap = SimpleServerHandler.deviceStatusMap;
        if (deviceStatusMap.containsKey("d9006bc4cdfbe96")){
            String json = deviceStatusMap.get("d9006bc4cdfbe96");
            IotDeviceStatusProductA deviceStatusProductA = new Gson().fromJson(json,IotDeviceStatusProductA.class);
            if (deviceStatusProductA.power == 1){
                model.addObject("power","ON");
            }
        }

        if (productKey == null || deviceId == null || deviceSecret == null){
            //空白页面
            return model;
        }

        IotDevice iotDevice;
        iotDevice = new IotDevice();
        iotDevice.productKey = productKey;
        iotDevice.deviceId = deviceId;
        iotDevice.deviceSecret = deviceSecret;

        if (power != null) {

            if (power.equals("ON")) {
                model.addObject("power","ON");
                //下发命令：开灯
                //SendMessage.setAll(true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SendMessage.sendMessageToDevice(iotDevice,"ON");
                    }
                }).run();

            }

            if (power.equals("OFF")) {
                model.addObject("power","OFF");
                //下发命令：关灯
                //SendMessage.setAll(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SendMessage.sendMessageToDevice(iotDevice,"OFF");
                    }
                }).run();
            }

        }

        return model;

    }


}

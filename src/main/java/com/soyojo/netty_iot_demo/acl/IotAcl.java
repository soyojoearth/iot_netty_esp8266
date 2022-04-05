package com.soyojo.netty_iot_demo.acl;

import com.soyojo.netty_iot_demo.bean.IotDevice;

public class IotAcl {
    public boolean verifyDevice(IotDevice iotDevice){

        //验证设备合法性，验证通过，返回true（通常这里是需要连接数据库校验的，这里仅为了演示方便）
        if (iotDevice.deviceId.equals("d9006bc4cdfbe96") && iotDevice.deviceSecret.equals("d9006b9cfdf3a27f84c4cdfbe96aad01")){
            return true;
        }

        return false;

    }
}

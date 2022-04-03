package com.soyojo.netty_iot_demo.bean;

/**
 * @author soyojo.earth@gmail.com
 * @address Shenzhen, China
 */
public class IotDevice {
    public String productKey;
    public String deviceId;
    public String deviceSecret;

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceSecret() {
        return deviceSecret;
    }

    public void setDeviceSecret(String deviceSecret) {
        this.deviceSecret = deviceSecret;
    }
}

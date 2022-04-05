#include <ESP8266WiFi.h>
#include "Ticker.h"
 
#define led 2 //发光二极管连接在8266的GPIO2上
#define btn 0

const char *ssid     = "tomato";//wifi名称
const char *password = "54321234";//wifi密码

//const char *host = "iot-test.xxxx.com";//写域名就可以利用DNS的不同地域分流和负载均衡。
const char *host = "192.168.18.103";//可以写域名，也可以写IP。

const int tcpPort = 8081;//服务器端口号

//设备三元数据
const char *productKey    = "a1a7Azal8hY";
const char *deviceId      = "d9006bc4cdfbe96";
const char *deviceSecret  = "d9006b9cfdf3a27f84c4cdfbe96aad01";

WiFiClient client;

Ticker ticker;

int power = 1;//开机默认灯亮
 
void setup()
{
    Serial.begin(115200);    
    pinMode(led,OUTPUT);
    delay(10);
    Serial.println();
    Serial.print("Connecting to ");//会通过usb转tll模块发送到电脑，通过ide集成的串口监视器可以获取数据。
    Serial.println(ssid);
 
    WiFi.begin(ssid, password);//启动
 
     //在这里检测是否成功连接到目标网络，未连接则阻塞。
    while (WiFi.status() != WL_CONNECTED)
    {
        delay(500);
    }
 
    //几句提示
    Serial.println("");
    Serial.println("WiFi connected");
    Serial.println("IP address: ");
    Serial.println(WiFi.localIP());

    ticker.attach(60,heartbeat);//60秒一次心跳

    if(power){
        digitalWrite(led, LOW);//开
    }
    else{
        digitalWrite(led, HIGH);//关
    }
     
}
 
void loop()
{
    //按键控制灯
    if(digitalRead(btn) == LOW){
        delay(20);//去抖
        if(digitalRead(btn) == LOW){
          while(digitalRead(btn) == LOW);
          if(power){
            power = 0;
            digitalWrite(led, HIGH);//关
          }
          else{
            power = 1;
            digitalWrite(led, LOW);//开
          }
          updateData();//更新数据到服务器
        }
    }
  
    if (!client.connected())//若未连接到服务端，则客户端进行连接。
    {
        if (!client.connect(host, tcpPort))//实际上这一步就在连接服务端，如果连接上，该函数返回true
        {
            Serial.println("connection....");
            delay(500);
        }
    }
    else
    {
        if(client.available())//available()表示是否可以获取到数据
        {
          String content = client.readStringUntil('\n');
          //接收一段文本，通常是json，此处为了演示方便随便写了
          if(content.equals("OFF")){
              power = 0;//关
              digitalWrite(led, HIGH);
              Serial.println("LED OFF");
              updateData();//更新数据到服务器
          }
          else if(content.equals("ON"))
          {
              power = 1;//开
              digitalWrite(led, LOW);
              Serial.println("LED ON");
              updateData();//更新数据到服务器
          }


          if(content.equals("who"))//需要登录验证
          {
              answerWho();
          }
          else if(content.equals("loginSuccess"))//验证通过（已登录）
          {
              updateData();//更新数据到服务器
          }
          else if(content.equals("loginFail"))//验证不通过（登陆失败）
          {
              Serial.println("loginFail\n");
          }
          else if(content.equals("AccessDenied"))//访问禁止（未登录）
          {
              Serial.println("access denied\n");
          }

        }
    }
}

void heartbeat(){
    if (client.connected())
    {
        Serial.println("heartbeat");
        client.write("ping");
        client.write("\n");
    }
}

void answerWho(){
        String msg = "{\"type\":\"login\",\"productKey\":\""+String(productKey)+"\",\"deviceId\":\""+String(deviceId)+"\",\"deviceSecret\":\""+String(deviceSecret)+"\"}";
        int len = msg.length()+1;
        char buf[len];
        msg.toCharArray(buf,len);
        client.write(buf);
        client.write("\n");
}

void updateData(){
    if (client.connected())
    {
        Serial.println("updateData");
        //更新数据到服务器
        String msg = "{\"type\":\"update\",\"power\":0}";
        if(power){
            msg = "{\"type\":\"update\",\"power\":1}";
        }
        int len = msg.length()+1;
        char buf[len];
        msg.toCharArray(buf,len);
        Serial.println(buf);
        client.write(buf);
        client.write("\n");
    }
}

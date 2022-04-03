# 简单的Iot服务器程序(Netty)和ESP8266程序(Arduino)，可以控制一个LED灯。



1、服务器端由Spingboot+Netty构成；

2、硬件是ESP8266开发板；（常见的那种，见下面的图）

3、由于是简单的演示，所以App啥的就不做了，仅在网页端控制；



功能：

​		手机端可以开关设备上的LED灯，设备上按键开关LED后，手机端可以显示LED的开关状态。

使用方式：

​		1、用IDEA打开源码，运行；

​		2、用Arduino编辑器打开ESP8266程序，修改局域网IP为电脑IP，然后烧录；

​		3、浏览器输入 http://<电脑局域网IP地址>:8080/set/led ，就可以控制设备LED灯的开关了。

效果图：

<img src="image\led_off.png" alt="led_off" style="zoom:50%;" />

<img src="image\led_on.png" alt="led_on" style="zoom:50%;" />

<img src="image\screen.png" alt="screen" style="zoom:50%;" />


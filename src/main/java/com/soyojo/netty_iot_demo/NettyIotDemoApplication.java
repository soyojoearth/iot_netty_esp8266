package com.soyojo.netty_iot_demo;

import com.soyojo.netty_iot_demo.netty.NettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author soyojo.earth@gmail.com
 * @address Shenzhen, China
 */
@SpringBootApplication
public class NettyIotDemoApplication {

    public static void main(String[] args) {

        ScheduledExecutorService ses = Executors.newScheduledThreadPool(4);

        ses.schedule(new Runnable() {
            @Override
            public void run() {
                new NettyServer(8081);
            }
        }, 1, TimeUnit.SECONDS);

        ses.schedule(new Runnable() {
            @Override
            public void run() {
                SpringApplication.run(NettyIotDemoApplication.class, args);
            }
        }, 1, TimeUnit.SECONDS);

    }

}

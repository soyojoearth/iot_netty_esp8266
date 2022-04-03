package com.soyojo.netty_iot_demo.controller.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author soyojo.earth@gmail.com
 * @address Shenzhen, China
 */
@RestController
public class ApiPingController {

    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public String exec() {
        return "pong";
    }

}
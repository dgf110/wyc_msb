package com.mashibing.apipassenger.controller;

import com.mashibing.apipassenger.feign.ServiceSms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/test")
public class TestCallServiceSmsController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/call")
    public String testCall(){

        return restTemplate.getForObject("http://service-sms/test/sms-test",String.class);
    }

    @GetMapping("/test")
    public String testCall1(){

        return "api-passenger";
    }


    @Autowired
    private ServiceSms serviceSms;

    @GetMapping("/feign")
    public String feign(){
        return serviceSms.feign();
    }

//    @Autowired
//    private DynamicFeignClient dynamicFeignClient;

//    @GetMapping("/dy-feign")
//    public String dyFeign(String url){
//        return dynamicFeignClient.call("service-sms",url,"get");
//    }
}

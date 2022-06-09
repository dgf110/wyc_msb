package com.mashibing.cloudzuul.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.stereotype.Service;

@Service
public class SentinelService {

    /**
     * 正常方法
     * @return
     */
    @SentinelResource(value = "SentinelService.success",blockHandler = "fail")
    public String success(){
        System.out.println("success 正常请求");
        return "success";
    }

    /**
     * 阻塞住的方法
     * @return
     */
    public String fail(BlockException e){
        System.out.println("阻塞");
        return "fail";
    }
}

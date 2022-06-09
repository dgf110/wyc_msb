package com.mashibing.servicevaluation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.mashibing.servicevaluation.mapper")
@EnableAsync
public class ServiceValuationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceValuationApplication.class, args);
    }

}

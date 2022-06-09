package com.mashibing.cloudschedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CloudScheduleApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudScheduleApplication.class, args);
    }

}

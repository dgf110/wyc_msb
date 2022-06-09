package com.mashibing.cloudschedule.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ProduceTask {
    /**
     *  每五秒执行一次
     */
    @Scheduled(cron="0/5 * * * * ?")
    public void executeFileDownLoadTask() {
        System.out.println("定时任务启动");
    }
}
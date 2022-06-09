package com.mashibing.cloudzuul;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableZuulProxy
public class CloudZuulApplication {

    public static void main(String[] args) throws Exception {
        init();
        SpringApplication.run(CloudZuulApplication.class, args);
    }

    private static void init(){
        // 所有限流规则的合集
        List<FlowRule> rules = new ArrayList<>();

        FlowRule rule = new FlowRule();
        // 资源名称
        rule.setResource("SentinelService.success");
        // 限流的类型
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // 2 qps
        rule.setCount(2);

        rules.add(rule);

        FlowRuleManager.loadRules(rules);

    }

    @Bean
    public SentinelResourceAspect sentinelResourceAspect(){
        return new SentinelResourceAspect();
    }

}


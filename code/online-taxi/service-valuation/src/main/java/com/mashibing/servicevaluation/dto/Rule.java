package com.mashibing.servicevaluation.dto;

import lombok.Data;

/**
 * 计价规则
 */
@Data
public class Rule {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 计费规则主键类
     */
    private KeyRule keyRule;

    /**
     * 基础计费
     */
    private BasicRule basicRule;

    /**
     * 计费方法
     */
    private PriceRule priceRule;

    /**
     * 远途服务费
     */
    private BeyondRule beyondRule;

    /**
     * 夜间服务费
     */
    private NightRule nightRule;
}

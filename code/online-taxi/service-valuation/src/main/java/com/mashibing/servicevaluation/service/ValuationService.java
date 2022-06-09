package com.mashibing.servicevaluation.service;

import java.math.BigDecimal;

public interface ValuationService {
    /**
     *计算预估价格
     * @param orderId
     * @return
     */
   BigDecimal calcForecastPrice(Long orderId);
}

package com.mashibing.servicevaluation.controller;

import com.mashibing.internalcommon.constant.BusinessInterfaceStatus;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.servicevaluation.service.ValuationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;


@RestController
@RequestMapping("/valuation")
@RequiredArgsConstructor
public class ValuationController {
    private static final String ERR_CALC_FORECAST_PRICE = "订单预估价格计算错误";

    @NonNull
    private ValuationService valuationService;

    /**
     * 估价
     * @param orderId
     * @return
     */
    @GetMapping("/forecast")
    public ResponseResult valuation(Long orderId){
        BigDecimal price;
        try {
            price = valuationService.calcForecastPrice(orderId);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseResult.fail(BusinessInterfaceStatus.FAIL.getCode(), ERR_CALC_FORECAST_PRICE);
        }
        return ResponseResult.success(price);
    }
}

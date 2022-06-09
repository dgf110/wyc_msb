package com.mashibing.servicevaluation.service.impl;

import com.mashibing.internalcommon.constant.ChargingCategoryEnum;
import com.mashibing.servicevaluation.dto.DriverMeter;
import com.mashibing.servicevaluation.dto.PriceMeter;
import com.mashibing.servicevaluation.dto.Route;
import com.mashibing.servicevaluation.dto.Rule;
import com.mashibing.servicevaluation.entity.OrderRulePrice;
import com.mashibing.servicevaluation.entity.OrderRulePriceDetail;
import com.mashibing.servicevaluation.service.ValuationService;
import com.mashibing.servicevaluation.task.ValuationRequestTask;
import com.mashibing.servicevaluation.task.ValuationTask;
import com.mashibing.servicevaluation.util.PriceHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
@Service
@Slf4j
@RequiredArgsConstructor
public class ValuationServiceImpl implements ValuationService {
    @NonNull
    private ValuationRequestTask valuationRequestTask;

    @NonNull
    private ValuationTask valuationTask;

    @Override
    public BigDecimal calcForecastPrice(Long orderId) {
        //生成驾驶参数
        DriverMeter driverMeter = generateDriveMeter(orderId, ChargingCategoryEnum.Forecast);
        //计算价格
        PriceMeter priceMeter = generatePriceMeter(driverMeter);

        BigDecimal totalPrice = priceMeter.getBasicPriceValue();

        return totalPrice;
    }

    /**
     * 生成价格参数
     * @param driverMeter
     * @return
     */
    private PriceMeter generatePriceMeter(DriverMeter driverMeter){
        //获取分段计价任务
        CompletableFuture<List<OrderRulePriceDetail>> calcSubsectionPrice = valuationTask.calcSubsectionPrice(driverMeter);

        //基础计价任务
        CompletableFuture<OrderRulePrice> calcMasterPrice = valuationTask.calcMasterPrice(driverMeter);

        //基础计价任务
        BigDecimal price =calcSubsectionPrice.thenCombine(calcMasterPrice,(d,m) -> {
            //计算其他价格
            valuationTask.calcOtherPrice(driverMeter, m, d);

            //计算价格合计
            BigDecimal totalPrice = PriceHelper.add(m.getBasePrice(), m.getNightPrice(), m.getBeyondPrice(), m.getPathPrice(), m.getDurationPrice());

            //最低消费补足
            m.setSupplementPrice(BigDecimal.ZERO);
            if (totalPrice.compareTo(m.getLowestPrice()) < 0) {
                m.setSupplementPrice(PriceHelper.subtract(m.getLowestPrice(), totalPrice));
                totalPrice = m.getLowestPrice();
            }

            m.setTotalPrice(totalPrice);

            return m.getTotalPrice();
        }).join();

        //设置计算结果
        PriceMeter priceMeter = new PriceMeter();
        priceMeter.setRulePrice(calcMasterPrice.join()).setRulePriceDetails(calcSubsectionPrice.join())
               .setBasicPriceValue(price).setRuleId(driverMeter.getRule().getId());

        return priceMeter;
    }


    /**
     * 生成驾驶参数
     * @param orderId
     * @return
     */
    private DriverMeter generateDriveMeter(Long orderId, ChargingCategoryEnum categoryEnum){
        //获取驾驶规则
        Rule rule = valuationRequestTask.requestRule(orderId);
        DriverMeter driverMeter = new DriverMeter(categoryEnum);

        //todo 根据orderId获取order
//        driverMeter.setOrder(requestOrder);
        Route route = valuationRequestTask.requestRoute(driverMeter);
        driverMeter.setRoute(route).setRule(rule);
        return driverMeter;
    }
}

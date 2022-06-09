package com.mashibing.servicevaluation.task;

import com.mashibing.servicevaluation.dto.DriverMeter;
import com.mashibing.servicevaluation.dto.Rule;
import com.mashibing.servicevaluation.dto.TimeMeter;
import com.mashibing.servicevaluation.entity.OrderRulePrice;
import com.mashibing.servicevaluation.entity.OrderRulePriceDetail;
import com.mashibing.servicevaluation.util.PriceHelper;
import com.mashibing.servicevaluation.util.TimeSlice;
import com.mashibing.servicevaluation.util.UnitConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ValuationTask {

    /**
     * 计算分段价格明细
     * @param driverMeter
     * @return
     */
    @Async
    public CompletableFuture<List<OrderRulePriceDetail>>calcSubsectionPrice(DriverMeter driverMeter){
        //行驶开始到结束的时间段
        TimeSlice timeSlice = generateTimeSlice(driverMeter);
        List<OrderRulePriceDetail> list = Optional.ofNullable(driverMeter.getRule().getPriceRule().getTimeRules()).orElse(new ArrayList<>()).stream().map(r -> {
            OrderRulePriceDetail detail = new OrderRulePriceDetail();
            BeanUtils.copyProperties(r, detail);
            detail.setOrderId(driverMeter.getOrder().getId());
            detail.setCategory(driverMeter.getCategoryEnum().getCodeAsString());
            detail.setStartHour(r.getStart());
            detail.setEndHour(r.getEnd());

            //设置计算用参数
            TimeMeter.TimePriceUnit unit = generateTimePriceUnit(driverMeter);
            unit.setStart(LocalTime.of(detail.getStartHour(),0,0));
            unit.setEnd(LocalTime.of(detail.getEndHour(),0,0));
            unit.setPerMeterPrice(UnitConverter.kiloToMeterPrice(detail.getPerKiloPrice()));
            unit.setPerSecondPrice(UnitConverter.minuteToSecondPrice(detail.getPerMinutePrice()));

            //获取计算结果并填充
            TimeMeter.TimePriceResult result = TimeMeter.measure(timeSlice, unit);
            detail.setDuration(UnitConverter.secondToMinute(result.getDuration()));
            detail.setTimePrice(result.getTimePrice());
            detail.setDistance(UnitConverter.meterToKilo(result.getDistance()));
            detail.setDistancePrice(result.getDistancePrice());

            return detail;
        }).collect(Collectors.toList());

        return new AsyncResult<>(list).completable();
    }

    /**
     * 计算基本价格
     *
     * @param driveMeter 行驶信息
     * @return 基本价格
     */
    @Async
    public CompletableFuture<OrderRulePrice> calcMasterPrice(DriverMeter driveMeter) {
        OrderRulePrice rulePrice = new OrderRulePrice();
        Rule rule = driveMeter.getRule();

        //key信息
        rulePrice.setOrderId(driveMeter.getOrder().getId());
        rulePrice.setCategory(driveMeter.getCategoryEnum().getCodeAsString());
        rulePrice.setTotalDistance(UnitConverter.meterToKilo(driveMeter.getTotalDistance()));
        rulePrice.setTotalTime(UnitConverter.secondToMinute(driveMeter.getTotalTime()));
        rulePrice.setCityCode(rule.getKeyRule().getCityCode());
        rulePrice.setCityName(rule.getKeyRule().getCityName());
        rulePrice.setServiceTypeId(rule.getKeyRule().getServiceTypeId());
        rulePrice.setServiceTypeName(rule.getKeyRule().getServiceTypeName());
        rulePrice.setChannelId(rule.getKeyRule().getChannelId());
        rulePrice.setChannelName(rule.getKeyRule().getChannelName());
        rulePrice.setCarLevelId(rule.getKeyRule().getCarLevelId());
        rulePrice.setCarLevelName(rule.getKeyRule().getCarLevelName());

        //基础价格
        rulePrice.setBasePrice(rule.getBasicRule().getBasePrice());
        rulePrice.setBaseKilo(rule.getBasicRule().getKilos());
        rulePrice.setBaseMinute(rule.getBasicRule().getMinutes());
        rulePrice.setLowestPrice(rule.getBasicRule().getLowestPrice());
        rulePrice.setPerKiloPrice(rule.getPriceRule().getPerKiloPrice());
        rulePrice.setPerMinutePrice(rule.getPriceRule().getPerMinutePrice());

        //夜间价格
        rulePrice.setNightTime(0D);
        rulePrice.setNightDistance(0D);
        rulePrice.setNightPrice(BigDecimal.ZERO);
        if (rule.getNightRule().getStart() != null && rule.getNightRule().getEnd() != null) {
            rulePrice.setNightStart(rule.getNightRule().getStart());
            rulePrice.setNightEnd(rule.getNightRule().getEnd());
            rulePrice.setNightPerKiloPrice(rule.getNightRule().getPerKiloPrice());
            rulePrice.setNightPerMinutePrice(rule.getNightRule().getPerMinutePrice());

            //计算夜间价格
            TimeMeter.TimePriceUnit unit = generateTimePriceUnit(driveMeter);
            unit.setStart(UnitConverter.dateToLocalTime(rulePrice.getNightStart()));
            unit.setEnd(UnitConverter.dateToLocalTime(rulePrice.getNightEnd()));
            unit.setPerMeterPrice(UnitConverter.kiloToMeterPrice(rulePrice.getNightPerKiloPrice()));
            unit.setPerSecondPrice(UnitConverter.minuteToSecondPrice(rulePrice.getNightPerMinutePrice()));

            TimeMeter.TimePriceResult result = TimeMeter.measure(generateTimeSlice(driveMeter), unit);
            rulePrice.setNightTime(UnitConverter.secondToMinute(result.getDuration()));
            rulePrice.setNightDistance(UnitConverter.meterToKilo(result.getDistance()));
            rulePrice.setNightPrice(PriceHelper.add(result.getDistancePrice(), result.getTimePrice()));
        }

        //远途价格
        rulePrice.setBeyondStartKilo(rule.getBeyondRule().getStartKilo());
        rulePrice.setBeyondPerKiloPrice(rule.getBeyondRule().getPerKiloPrice());
        rulePrice.setBeyondDistance(PriceHelper.subtract(rulePrice.getTotalDistance(), rulePrice.getBeyondStartKilo()).doubleValue());
        rulePrice.setBeyondPrice(PriceHelper.multiply(rulePrice.getBeyondPerKiloPrice(), rulePrice.getBeyondDistance()));

        return new AsyncResult<>(rulePrice).completable();
    }

    /**
     * 计算其他价格
     *
     * @param driveMeter 驾驶参数
     * @param master     基础计价任务结果
     * @param details    分段计价任务结果
     */
    public void calcOtherPrice(DriverMeter driveMeter, OrderRulePrice master, List<OrderRulePriceDetail> details) {
        //是否采用基础套餐的计费规则
        if (driveMeter.getRule().getBasicRule().isBasicCharging()) {
            master.setRestDistance(0D);
            master.setRestDistancePrice(BigDecimal.ZERO);
            master.setRestDuration(0D);
            master.setRestDurationPrice(BigDecimal.ZERO);

            master.setPath(PriceHelper.subtract(master.getTotalDistance(), master.getBaseKilo()).doubleValue());
            master.setPathPrice(PriceHelper.multiply(master.getPerKiloPrice(), master.getPath()));
            master.setDuration(PriceHelper.subtract(master.getTotalTime(), master.getBaseMinute()).doubleValue());
            master.setDurationPrice(PriceHelper.multiply(master.getPerMinutePrice(), master.getDuration()));
        } else {
            //计算时间段外的价格
            master.setRestDistance(PriceHelper.subtract(master.getTotalDistance(), details.stream().mapToDouble(OrderRulePriceDetail::getDistance).sum()).doubleValue());
            master.setRestDistancePrice(PriceHelper.multiply(master.getPerKiloPrice(), master.getRestDistance()));
            master.setRestDuration(PriceHelper.subtract(master.getTotalTime(), details.stream().mapToDouble(OrderRulePriceDetail::getDuration).sum()).doubleValue());
            master.setRestDurationPrice(PriceHelper.multiply(master.getPerMinutePrice(), master.getRestDuration()));

            master.setPath(master.getTotalDistance());
            master.setPathPrice(PriceHelper.add(master.getRestDistancePrice(), details.stream().map(OrderRulePriceDetail::getDistancePrice).reduce(BigDecimal.ZERO, BigDecimal::add)));
            master.setDuration(master.getTotalTime());
            master.setDurationPrice(PriceHelper.add(master.getRestDurationPrice(), details.stream().map(OrderRulePriceDetail::getTimePrice).reduce(BigDecimal.ZERO, BigDecimal::add)));
        }

    }

    private TimeSlice generateTimeSlice(DriverMeter driverMeter){
        TimeSlice timeSlice = new TimeSlice();
        timeSlice.setX(driverMeter.getStartTime());
        timeSlice.setY(timeSlice.getX().plusSeconds((long)Math.ceil(driverMeter.getTotalTime())));
        return timeSlice;
    }

    /**
     * 获取单位价格
     * @return
     */
    private TimeMeter.TimePriceUnit generateTimePriceUnit(DriverMeter driverMeter){
        TimeMeter.TimePriceUnit unit = null;
        switch (driverMeter.getCategoryEnum()) {
            case Forecast:
                BigDecimal speed = BigDecimal.valueOf(driverMeter.getTotalDistance()).divide(BigDecimal.valueOf(driverMeter.getTotalTime()));
                unit = TimeMeter.TimePriceUnit.instanceByForecast(speed.doubleValue());
            default:
                break;
        }
        return unit;
    }
}

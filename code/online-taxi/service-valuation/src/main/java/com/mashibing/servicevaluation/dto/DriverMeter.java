package com.mashibing.servicevaluation.dto;

import com.mashibing.internalcommon.constant.ChargingCategoryEnum;
import com.mashibing.internalcommon.entity.Order;
import com.mashibing.servicevaluation.util.UnitConverter;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
@Accessors(chain = true)
@RequiredArgsConstructor
public class DriverMeter {

    /**
     * 订单
     */
    private Order order;

    /**
     * 计价规则
     */
    private Rule rule;

    /**
     * 预估时距离测量结果：没行驶之前
     *
     */
    private Route route;

    /**
     *计价规则种类枚举
     */
    @NonNull
    private ChargingCategoryEnum categoryEnum;


    /**
     * 返回订单开始时间
     * @return
     */
    public LocalDateTime getStartTime(){
        return UnitConverter.dateToLocalDateTime(order.getStartTime());
    }

    /**
     *返回行驶时间(秒)
     * @return
     */
    public double getTotalTime(){
        return route.getDuration();
    }

    public double getTotalDistance(){
        Double meters = 0D;
        switch (categoryEnum) {
            case Forecast:
               meters = route.getDistance();
               break;
            default:
                break;
        }
        return Optional.ofNullable(meters).orElse(0D);
    }
}

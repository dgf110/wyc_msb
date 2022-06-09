package com.mashibing.servicevaluation.task;
//
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.entity.Order;
import com.mashibing.internalcommon.util.RestTemplateHepler;
import com.mashibing.servicevaluation.dao.OrderRuleMirrorDao;
import com.mashibing.servicevaluation.dto.DriverMeter;
import com.mashibing.servicevaluation.dto.Route;
import com.mashibing.servicevaluation.dto.Rule;
import com.mashibing.servicevaluation.entity.OrderRuleMirror;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class ValuationRequestTask {

//    @NonNull
//    private RestTemplate restTemplate;

    @NonNull
    private OrderRuleMirrorDao orderRuleMirrorDao;

    /**
     * 根据订单id取得计价规则
     * @param orderId
     * @return
     */
    @SneakyThrows
    public Rule requestRule(Long orderId){
        Rule rule;
        try {
            OrderRuleMirror orderRuleMirror = orderRuleMirrorDao.selectByOrderId(orderId);
            String ruleMirror = orderRuleMirror.getRule();

            ObjectMapper objectMapper = new ObjectMapper();
            rule = objectMapper.readValue(ruleMirror, Rule.class);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("orderId={}, 解析RuleJson错误:", orderId, e);
            throw e;
        }
        return rule;
    }

    /**
     * 组装驾驶参数
     * @param driverMeter
     * @return
     */
    @SneakyThrows
    public Route requestRoute(DriverMeter driverMeter){
        Route route;
        try{
            Map<String, Object> map = new HashMap<>(4);
            map.put("originLongitude",driverMeter.getOrder().getStartLongitude());
            map.put("originLatitude",driverMeter.getOrder().getEndLatitude());
            map.put("destinationLongitude",driverMeter.getOrder().getEndLongitude());
            map.put("destinationLatitude",driverMeter.getOrder().getEndLatitude());

//            //todo 请求地图服务计算距离和时间
//            ResponseResult responseResult = restTemplate.getForObject("", ResponseResult.class, map);
//            route = RestTemplateHepler.parse(responseResult, Route.class);
//            if(route.getDistance() == null || route.getDuration() == null){
//                throw new Exception("Route内容为空:" + route);
//            }
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
        return null;
    }
}

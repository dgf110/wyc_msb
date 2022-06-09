package com.mashibing.cloudzuul.filter;

import com.google.common.util.concurrent.RateLimiter;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * @author 马士兵教育:晁鹏飞
 * @date
 */
@Component
public class LimitFilterPost extends ZuulFilter {

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return -9;
    }

    @Override
    public boolean shouldFilter() {
//        return RequestContext.getCurrentContext().sendZuulResponse();
        return false;
    }



    @Override
    public Object run() throws ZuulException {

        System.out.println("我是 后面 的 ");
        return null;
    }
}
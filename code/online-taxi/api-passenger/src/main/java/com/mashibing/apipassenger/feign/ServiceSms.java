package com.mashibing.apipassenger.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "service-sms")
public interface ServiceSms {
	
	@RequestMapping(value = "/test/sms-test-feign",method = RequestMethod.GET)
	public String feign();
	
}
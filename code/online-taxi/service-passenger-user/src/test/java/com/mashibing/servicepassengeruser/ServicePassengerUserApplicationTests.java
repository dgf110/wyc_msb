package com.mashibing.servicepassengeruser;


import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.dto.servicepassengeruser.request.LoginRequest;
import com.online.taxi.servicepassengeruser.ServicePassengerUserApplication;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URL;

/**
 * 乘客登陆 测试用例
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServicePassengerUserApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ServicePassengerUserApplicationTests {

    @LocalServerPort
    private int port;

    private URL base;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setUp() throws Exception {
        String url = String.format("http://localhost:%d/", port);
        System.out.println(String.format("port is : [%d]", port));
        this.base = new URL(url);
    }

    /**
     * 乘客登陆
     */
    @Test
    public void a1_passengerLogin() {
        System.out.println(" 》》》》》 乘客登陆测试 开始");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassengerPhone("12345678901");

        String url = this.base.toString() + "/auth/login";
        ResponseEntity<ResponseResult> response = restTemplate.postForEntity(url, loginRequest, ResponseResult.class);
        System.out.println(String.format(" 《《《《《 测试结果为：\r\n %s \r\n", response.getBody()));
    }

    /**
     * 乘客登出
     */
    @Test
    public void a2_passengerLogout() {
        System.out.println(" 》》》》》 乘客登出测试 开始");

        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI3NDkwMDg2ODU0ODU1ODg0ODAiLCJpYXQiOjE1OTg2MjExODd9.cYqPPNpmPEpgPX4_hIq7NjZLrWsk6v_I6IOBYqjA8qi2GpZ1xeplevlw0mvS01P1EJ1UASWnGKuuoTP1FARjcA";
        String url = this.base.toString() + "/auth/logout?token=" + token;
        ResponseEntity<ResponseResult> response = restTemplate.postForEntity(url, null, ResponseResult.class);

        System.out.println(String.format(" 《《《《《 测试结果为：\r\n %s \r\n", response.getBody()));
    }

}

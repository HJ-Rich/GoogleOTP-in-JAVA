package com.otp.client.controller;

import com.otp.client.util.OTPUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
public class AuthController {

    @PostMapping(path="/validate", produces="application/json;charset=UTF-8")
    public String validate(@RequestBody Map<String, String> data) throws IOException {

        data.put("url", "http://localhost:2032/validate");
        String result = OTPUtil.validate(data);

        System.out.println("recieved result : " + result);

        return result;
    }

    @PostMapping(path="/regist")
    public String regist(@RequestBody Map<String, String> data) throws Exception {

        data.put("url", "http://localhost:2032/regist");
        String result = OTPUtil.regist(data);

        System.out.println("recieved value : " + result);

        return result;
    }


}

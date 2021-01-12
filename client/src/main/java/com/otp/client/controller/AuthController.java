package com.otp.client.controller;

import com.otp.client.util.OTPUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
public class AuthController {

    public static final String agentURL = "http://localhost:2032/auth";

    @PostMapping(path="/auth", produces="application/json;charset=UTF-8")
    public String auth(@RequestBody Map<String, Object> data) throws IOException {

        String result = OTPUtil.getAuthResultFromAgent(agentURL, String.valueOf(data.get("tryKey")));

        return result;
    }

}

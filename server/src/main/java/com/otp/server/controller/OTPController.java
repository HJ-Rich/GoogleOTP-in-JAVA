package com.otp.server.controller;

import com.otp.server.util.OTPUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OTPController {

    // 서버 키. 인증 요청이 올 때마다 서버 키 값으로 일회용 비밀번호를 생성해 요청값과 비교하는데 사용한다.
    private static final String secretkey = "BR3WFXKBNUW7WQSAG5M6TCYSWTRJVZ3Z";

    @PostMapping(path="/auth", produces="application/json;charset=UTF-8")
    public boolean auth(@RequestBody String data){

        String key = OTPUtil.getTOTPCode(secretkey);
        data = data.trim();
        System.out.println("try key : " + data);
        System.out.println("sec key : " + key);
        System.out.println("검증 결과 " + data.equals(key));

        // 아래는 QR코드 이미지를 생성하는 메소드 예시.
        //String qrCode = OTPUtil.getGoogleAuthQRCode(secretkey, "Test", "SanhaIT");
        //Util.createQRCode(qrCode, "C:\\qr\\qr.png", 200,200);

        return data.equals(key);
    }
}

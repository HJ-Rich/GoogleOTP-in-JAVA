package com.otp.server.controller;

import com.google.zxing.WriterException;
import com.otp.server.util.OTPUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
public class OTPController {

    @Autowired
    private ValueOperations<String, String> redis;

    // OTP 인증시도하는 메소드
    @PostMapping(path="/validate", produces="application/json;charset=UTF-8")
    public boolean validate(@RequestBody String data){

        String company = data.split(" ")[0];
        String userid = data.split(" ")[1];
        String tryKey = data.split(" ")[2];

        String validKey = OTPUtil.getTOTPCode(redis.get(company+":"+userid));
        boolean result = tryKey.equals(validKey);

        System.out.println(company + " : " + userid + " : " + tryKey + " : " + validKey);
        System.out.println("검증 결과 " + result);

        return result;
    }

    // 최초 OTP 등록을 위해 Redis 등록 및 QR이미지 생성하는 메소드
    @PostMapping(path="/regist", produces="application/json;charset=UTF-8")
    public String regist(@RequestBody String data) throws IOException, WriterException {

        String company = data.split(" ")[0];
        String userid = data.split(" ")[1];
        String redisKey = company+":"+userid;

        String secretKey = "";
        if(redis.get(redisKey) == null) {
            secretKey = OTPUtil.generateSecretKey();
            redis.set(redisKey, secretKey);
        }
        else {
            secretKey = redis.get(redisKey);
        }

        System.out.println("redisKey : " + redisKey);
        System.out.println("secretKey : " + secretKey);

        String qrCode = OTPUtil.getGoogleAuthenticatorBarCode(secretKey, userid, company);
        File file = new File("src/main/resources/static/qr/"+company+userid+".png");
        System.out.println("QR이미지 저장 경로 : " + file.getAbsolutePath());
        OTPUtil.createQRCode(qrCode, file.getAbsolutePath(), 200,200);

        return secretKey;
    }

    // QR코드 이미지를 리턴하는 메소드
    @GetMapping(path="/qr/{redisKey}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getQRImage(@PathVariable String redisKey) throws IOException {

        System.out.println(redisKey);
        if(redis.get(redisKey) == null) return null;

        File file = new File("src/main/resources/static/qr/"+redisKey.replace(":", "")+".png");
        System.out.println("요청 이미지 : " + file.getAbsolutePath());

        return Files.readAllBytes(file.toPath());
    }

}

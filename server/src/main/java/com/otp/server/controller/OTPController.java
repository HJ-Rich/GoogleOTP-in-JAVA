package com.otp.server.controller;

import com.otp.server.mail.RichMailSender;
import com.otp.server.util.OTPUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@EnableAsync
@RestController
public class OTPController {

    @Autowired
    private ValueOperations<String, String> redis;

    @Autowired
    private RichMailSender richMailSender;

    // OTP 인증시도하는 메소드
    @PostMapping(path="/validateGoogleOTP", produces="application/json;charset=UTF-8")
    public boolean validate(@RequestBody String data){

        String company = data.split(" ")[0];
        String userid = data.split(" ")[1];
        String tryKey = data.split(" ")[2];

        String secretKeyFromRedis = redis.get(company+":"+userid);
        String validKey = OTPUtil.getTOTPCode(secretKeyFromRedis);
        boolean result = tryKey.equals(validKey);

        System.out.println(company + " : " + userid + " : " + tryKey + " : " + validKey);
        System.out.println("검증 결과 " + result);

        return result;
    }

    // 최초 OTP 등록을 위해 Redis 등록 및 QR이미지 생성하는 메소드
    @PostMapping(path="/registerGoogleOTP", produces="application/json;charset=UTF-8")
    public String registerGoogleOTP(@RequestBody String data) throws MessagingException {

        String company = data.split(" ")[0];
        String userid = data.split(" ")[1];
        String redisKey = company+":"+userid;

        String secretKey = "";
        boolean generated;
        if(redis.get(redisKey) == null) {
            secretKey = OTPUtil.generateSecretKey();
            redis.set(redisKey, secretKey);
            generated = true;
        }
        else {
            generated = false;
        }

        System.out.println("redisKey : " + redisKey);
        System.out.println("secretKey : " + secretKey);
        System.out.println("Existence : " + generated);

        if(generated) {
            String qrCode = OTPUtil.getGoogleAuthenticatorBarCode(secretKey, userid, company);
            OTPUtil.createQRCode(qrCode, file.getAbsolutePath(), 200,200);
        }
        File file = new File("src/main/resources/static/qr/"+company+userid+".png");
        System.out.println("QR이미지 저장 경로 : " + file.getAbsolutePath());

        Long start = System.currentTimeMillis();
        richMailSender.upradeMessage("target@mail.com", "title", "content", "filePath");
        Long finish = System.currentTimeMillis();

        Long sub = finish - start;
        System.out.println(sub/1000.0 + "초 소요");

        return "true";
    }

    // QR코드 이미지를 리턴하는 메소드
    @PostMapping(path="/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getQRImage(@RequestBody String redisKey) throws IOException {

        System.out.println(redisKey);
        if(redis.get(redisKey) == null) return null;

        File file = new File("src/main/resources/static/qr/"+redisKey.replace(":", "")+".png");
        System.out.println("요청 이미지 : " + file.getAbsolutePath());

        return Files.readAllBytes(file.toPath());
    }

}

package com.otp.client.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class OTPUtil {

    public static String getAuthResultFromAgent(String agentURL, String tryKey) throws IOException {
        URL obj = new URL(agentURL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is Post
        con.setRequestMethod("POST");

        // 연결된 connection 에서 출력도 하도록 설정
        con.setDoOutput(true);
        con.setDoInput(true);
        //add request header 헤더를 만들어주는것.
        con.setRequestProperty("Cache-Control", "no-cache");
        con.setRequestProperty("Content-Type", "application/json;charset=utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Accept-Charset", "UTF-8");
        con.setRequestProperty("Accept", "*/*");
        con.setRequestProperty("X-Requested-With", "XMLHttpRequest");

        // - 파라미터의 값으로 한국어 등을 송신하는 경우는 URL 인코딩을 해야 함.
        OutputStream os = con.getOutputStream();
        os.write( (tryKey).getBytes("utf-8") );
        os.flush();
        os.close();

        //리턴 파라미터 출력
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        String result = "";
        while ((inputLine = in.readLine()) != null) {
            result += inputLine;
        }
        in.close();

        con.disconnect();
        return result;
    }

}

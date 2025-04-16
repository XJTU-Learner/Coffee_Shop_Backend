package org.xjtu_learner.coffee_shop.service.impl;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class CallMeBotService {
    private  final String BASE_URL = "https://api.callmebot.com/text.php";


    //使用电报机器人发送信息
    public  String sendMessage(String user, String text) throws IOException {
        // 构建查询参数，只包含user和text
        String query = String.format(
                "user=%s&text=%s",
                URLEncoder.encode(user, StandardCharsets.UTF_8.toString()),
                URLEncoder.encode(text, StandardCharsets.UTF_8.toString())
        );

        URL url = new URL(BASE_URL + "?" + query);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        // 检查响应码
        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("HTTP请求失败，响应码: " + responseCode);
        }
        // 读取响应
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }
}

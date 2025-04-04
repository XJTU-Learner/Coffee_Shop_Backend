package org.xjtu_learner.coffee_shop.config;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("aliyun.dysms")
public class AliyunSmsConfig {
    private static final String endpoint = "dysmsapi.aliyuncs.com";
    private String accessKeyId;
    private String accessKeySecret;

    @Bean
    public Client smsClient() throws Exception {
        Config config = new Config()
                .setEndpoint(endpoint)
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        return new Client(config);
    }
}

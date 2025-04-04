package org.xjtu_learner.coffee_shop.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import org.springframework.stereotype.Component;
import org.xjtu_learner.coffee_shop.common.exception.CommonException;

import java.util.HashMap;

import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.SMS_FAILED;

@Component
public class AliyunSmsService {

    private final Client smsClient;


    public AliyunSmsService(Client smsClient) {
        this.smsClient = smsClient;
    }

    public void sendVerificationCode(String mobile, String code) {
        String param = JSONUtil.toJsonStr(new JSONObject().set("code", code));
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers(mobile)
                .setSignName("阿里云通信")
                .setTemplateCode("测试专用")
                .setTemplateParam(param);
        try {
            SendSmsResponse sendSmsResponse = smsClient.sendSms(sendSmsRequest);
            String bizId = sendSmsResponse.getBody().getBizId();
            if (StrUtil.isBlank(bizId)) {
                throw new CommonException("验证码发送失败：" + sendSmsResponse.getBody().getMessage(), SMS_FAILED);
            }
        } catch (Exception e) {
            throw new CommonException(e.getMessage(), SMS_FAILED);
        }
    }
}

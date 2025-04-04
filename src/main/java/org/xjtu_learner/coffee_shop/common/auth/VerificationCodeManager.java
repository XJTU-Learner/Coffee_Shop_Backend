package org.xjtu_learner.coffee_shop.common.auth;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.util.pattern.PatternParseException;
import org.xjtu_learner.coffee_shop.common.utils.RegexUtil;
import org.xjtu_learner.coffee_shop.service.impl.AliyunSmsService;

import java.util.concurrent.TimeUnit;

import static org.xjtu_learner.coffee_shop.common.constant.RedisConstant.SESSION_CODE_KEY;
import static org.xjtu_learner.coffee_shop.common.constant.RedisConstant.SESSION_CODE_TTL;

@Slf4j
@Component
public class VerificationCodeManager {

    private final StringRedisTemplate stringRedisTemplate;
    private final AliyunSmsService aliyunSmsService;

    public VerificationCodeManager(StringRedisTemplate stringRedisTemplate, AliyunSmsService aliyunSmsService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.aliyunSmsService = aliyunSmsService;
    }

    public void sendCode(String prefix,String mobile) {

        String code = RandomUtil.randomNumbers(6);

//        aliyunSmsService.sendVerificationCode(mobile,code);
        log.info("验证码发送成功：{}", code);

        // 保存验证码到redis ({SESSION_CODE_KEY:mobile} -> {code})
        stringRedisTemplate.opsForValue().set(SESSION_CODE_KEY + prefix + mobile, code, SESSION_CODE_TTL, TimeUnit.MINUTES);
    }

    public boolean verifyCode(String prefix, String mobile, String code) {
        // 从redis获取code
        String correctCode = stringRedisTemplate.opsForValue().get(SESSION_CODE_KEY + prefix + mobile);
        return correctCode != null && correctCode.equals(code);
    }
}

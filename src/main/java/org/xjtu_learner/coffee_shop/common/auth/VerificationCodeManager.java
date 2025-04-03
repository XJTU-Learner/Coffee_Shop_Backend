package org.xjtu_learner.coffee_shop.common.auth;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.xjtu_learner.coffee_shop.common.utils.RegexUtil;

import java.util.concurrent.TimeUnit;

import static org.xjtu_learner.coffee_shop.common.constant.RedisConstant.SESSION_CODE_KEY;
import static org.xjtu_learner.coffee_shop.common.constant.RedisConstant.SESSION_CODE_TTL;

@Slf4j
@Component
public class VerificationCodeManager {

    private final StringRedisTemplate stringRedisTemplate;

    public VerificationCodeManager(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public boolean sendCode(String mobile) {
        if (RegexUtil.isPhoneInvalid(mobile)) {
            return false;
        }

        String code = RandomUtil.randomNumbers(6);
        //TODO:验证码发送服务
        log.debug("验证码发送成功：{}", code);

        // 保存验证码到redis ({SESSION_CODE_KEY:mobile} -> {code})
        stringRedisTemplate.opsForValue().set(SESSION_CODE_KEY + mobile, code, SESSION_CODE_TTL, TimeUnit.MINUTES);
        return true;
    }

    public boolean verifyCode(String mobile, String code) {
        // 从redis获取code
        String correctCode = stringRedisTemplate.opsForValue().get(SESSION_CODE_KEY + mobile);
        return correctCode != null && correctCode.equals(code);
    }
}

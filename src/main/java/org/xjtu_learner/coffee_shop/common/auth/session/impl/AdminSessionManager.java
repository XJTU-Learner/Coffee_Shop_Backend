package org.xjtu_learner.coffee_shop.common.auth.session.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.BooleanUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.xjtu_learner.coffee_shop.common.auth.session.ISessionManager;
import org.xjtu_learner.coffee_shop.entity.dto.AdminDTO;
import org.xjtu_learner.coffee_shop.entity.po.Admin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.xjtu_learner.coffee_shop.common.constant.RedisConstant.*;

@Component
public class AdminSessionManager implements ISessionManager<Admin, AdminDTO> {
    private final String SESSION_PREFIX = ADMIN_SESSION_PREFIX;
    private final Long SESSION_TTL = ADMIN_SESSION_TTL;


    private final StringRedisTemplate stringRedisTemplate;

    public AdminSessionManager(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public String createSession(Admin admin) {
        String token = UUID.randomUUID(true).toString();
        String key = SESSION_PREFIX + token;
        AdminDTO dto = BeanUtil.copyProperties(admin, AdminDTO.class);
        Map<String, Object> map = BeanUtil.beanToMap(dto, new HashMap<>(), new CopyOptions()
                .setFieldValueEditor((fieldName, fieldValue) -> (fieldValue == null ? null : fieldValue.toString()))
        );
        // 将用户session信息保存到redis中（{ADMIN_SESSION_PREFIX:token} -> {AdminDTO}）
        stringRedisTemplate.opsForHash().putAll(key, map);
        stringRedisTemplate.expire(key, SESSION_TTL, TimeUnit.MINUTES);

        return token;
    }

    @Override
    public AdminDTO getSession(String token) {
        // 从redis获取用户session信息
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(SESSION_PREFIX + token);
        if (map.isEmpty()) {
            return null;
        }

        return BeanUtil.fillBeanWithMap(map, new AdminDTO(), false);
    }

    @Override
    public AdminDTO getSessionAndRefresh(String token) {
        AdminDTO adminDTO = getSession(token);
        if (adminDTO != null) {
            stringRedisTemplate.expire(SESSION_PREFIX + token, SESSION_TTL, TimeUnit.MINUTES);
        }

        return adminDTO;
    }

    @Override
    public void removeSession(String token) {
        stringRedisTemplate.delete(SESSION_PREFIX + token);
    }

    @Override
    public boolean haveSession(String token) {
        return BooleanUtil.isTrue(stringRedisTemplate.hasKey(SESSION_PREFIX + token));
    }
}

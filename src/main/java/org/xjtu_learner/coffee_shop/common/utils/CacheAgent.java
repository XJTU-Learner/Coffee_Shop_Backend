package org.xjtu_learner.coffee_shop.common.utils;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.xjtu_learner.coffee_shop.common.exception.CommonException;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.NOT_EXIST;
import static org.xjtu_learner.coffee_shop.common.constant.RedisConstant.*;

@Component
public class CacheAgent {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;

    public CacheAgent(StringRedisTemplate stringRedisTemplate, RedissonClient redissonClient) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.redissonClient = redissonClient;
    }

    public RBloomFilter<String> getBloomFilter(String name, long expectedInsertions, double fpp) {
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(name);
        if (!bloomFilter.isExists()) {
            bloomFilter.tryInit(expectedInsertions, fpp);
        }
        return bloomFilter;
    }


    public <R, ID> R queryById(
            String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback) {

        return queryById(keyPrefix, id, type, dbFallback, DEFAULT_CACHE_ID_TTL, TimeUnit.MINUTES);
    }

    /*
     * 通过互斥锁解决缓存雪崩问题
     * 通过设置空值解决缓存穿透问题
     * */
    public <R, ID> R queryById(
            String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit unit) {

        String key = keyPrefix + id;
        String json = stringRedisTemplate.opsForValue().get(key);

        // 判断json是否存在
        if (StrUtil.isNotBlank(json)) {
            // 缓存命中，直接返回
            return JSONUtil.toBean(json, type);
        }

        // 判断是否为为了解决缓存穿透问题而缓存的空对象""（由于前面已经进行了isNotBlank判断，如果这里json不为null，就只可能为""）
        if (json != null) {
            throw new CommonException("缓存穿透保护", NOT_EXIST);
        }

        // 到这里说明json==null，缓存未命中，互斥查询数据库并加入缓存
        R result = null;

        boolean isLock = tryLock(key);
        try {
            if (!isLock) {
                // 如果加锁失败则阻塞并重试
                Thread.sleep(50);
                return queryById(keyPrefix, id, type, dbFallback, time, unit);
            }
            // 获取锁成功
            result = dbFallback.apply(id);
            // 如果数据不存在则返回错误，并缓存空对象
            if (result == null) {
                stringRedisTemplate.opsForValue().set(key, "", CACHE_BLANK_TTL, TimeUnit.MINUTES);
                throw new CommonException("缓存穿透保护", NOT_EXIST);
            }
            // 数据存在则写入redis
            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(result), time, unit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            unlock(key);
        }
        return result;
    }

//    public <R, ID> List<R> queryListById(
//            String keyPrefix, ID id, Class<R> type, Supplier<Map<ID, R>> dbFallback) {
//
//        return queryListById(keyPrefix, id, type, dbFallback, DEFAULT_CACHE_LIST_TTL, TimeUnit.MINUTES);
//    }

    /*
    * 使用缓存查询列表类型结果
    * 实现分页
    * 实现模糊查询
    * */
//    public <R, ID> List<R> queryListById(
//            String keyPrefix, ID id, Class<R> type, Function<ID, List<R>> dbFallback, Long time, TimeUnit unit) {
//
//        // TODO:通过mysql实现分页和模糊查询并返回id列表
//
//        // TODO:批量从缓存中获取商品对象
//
//        // TODO:组装没有命中的商品ID
//
//        // TODO:批量从数据库查询未命中的商品信息列表，重新加载到缓存
//
//        // TODO:遍历商品ID列表，组装对象列表
//
//        // 从redis查询缓存
//        List<Object> values = stringRedisTemplate.opsForHash().values(key);
//
//        // 判断values是否存在
//        if (CollectionUtil.isNotEmpty(values)) {
//            // 缓存命中，直接返回（需要先将value中的json反序列化为bean）
//            return values.stream().map(value -> JSONUtil.toBean((String) value, type)).toList();
//        }
//
//        // 缓存未命中，互斥查询数据库并加入缓存
//        List<R> result = null;
//
//        boolean isLock = tryLock(key);
//        try {
//            if (!isLock) {
//                // 如果加锁失败则阻塞并重试
//                Thread.sleep(50);
//                return queryListById(key, type, dbFallback, time, unit);
//            }
//            // 获取锁成功
//            Map<ID, R> map = dbFallback.get();
//            // 如果数据不存在则返回错误
//            if (map == null) {
//                throw new CommonException("数据不存在", NOT_EXIST);
//            }
//            result = new ArrayList<>(map.values());
//            // 数据存在则写入redis（先将map中的value序列化为json）
//            Map<String, String> saveMap = map.entrySet().stream()
//                    .collect(Collectors.toMap(
//                            entry -> entry.getKey().toString(), // 调用键的 toString() 方法
//                            entry -> JSONUtil.toJsonStr(entry.getValue())   // 将value序列化为json
//                    ));
//            stringRedisTemplate.opsForHash().putAll(key, saveMap);
//            stringRedisTemplate.expire(key, time, unit);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        } finally {
//            unlock(key);
//        }
//        return result;
//    }

    public boolean tryLock(String key) {
        Boolean b = stringRedisTemplate.opsForValue().setIfAbsent(LOCK_PREFIX + key, "1", LOCK_TTL, TimeUnit.MINUTES);
        return BooleanUtil.isTrue(b);
    }

    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }
}

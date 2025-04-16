package org.xjtu_learner.coffee_shop.common.utils;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
public class BloomFilterUtil {

    public static final String BLOOMFILTER_SHOP_GOODS_RELATION = "SHOP_GOODS_RELATION";
    public static final Integer BLOOMFILTER_SHOP_GOODS_RELATION_SIZE = 2000;
    public static final Float BLOOMFILTER_SHOP_GOODS_RELATION_FPP = 0.01F;

    public static final String BLOOMFILTER_GOODS = "GOODS";
    public static final Integer BLOOMFILTER_GOODS_SIZE = 2000;
    public static final Float BLOOMFILTER_GOODS_FPP = 0.01F;



    private final RedissonClient redissonClient;

    public BloomFilterUtil(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public RBloomFilter<String> getBloomFilter(String name, long expectedInsertions, double fpp) {
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(name);
        if (!bloomFilter.isExists()) {
            bloomFilter.tryInit(expectedInsertions, fpp);
        }
        return bloomFilter;
    }
}

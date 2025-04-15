package org.xjtu_learner.coffee_shop.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.redisson.api.RBloomFilter;
import org.xjtu_learner.coffee_shop.common.utils.CacheAgent;
import org.xjtu_learner.coffee_shop.entity.po.Shop;
import org.xjtu_learner.coffee_shop.dao.ShopMapper;
import org.xjtu_learner.coffee_shop.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.xjtu_learner.coffee_shop.common.constant.RedisConstant.*;

/**
 * <p>
 * 门店表 服务实现类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-06
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Getter
    private RBloomFilter<String> bloomFilter;
    private final CacheAgent cacheAgent;

    public ShopServiceImpl(CacheAgent cacheAgent) {
        this.cacheAgent = cacheAgent;
    }

    @PostConstruct
    void init(){
        this.bloomFilter = cacheAgent.getBloomFilter(BLOOMFILTER_SHOP_GOODS_RELATION, BLOOMFILTER_SHOP_GOODS_RELATION_SIZE, BLOOMFILTER_SHOP_GOODS_RELATION_FPP);
        // 初始化布隆过滤器
        List<String> shopIdList = lambdaQuery().list().stream()
                .map(Shop::getId)
                .map(Object::toString)
                .toList();

        bloomFilter.add(shopIdList);
    }

}

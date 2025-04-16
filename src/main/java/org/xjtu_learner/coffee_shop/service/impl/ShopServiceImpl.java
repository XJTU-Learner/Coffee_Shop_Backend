package org.xjtu_learner.coffee_shop.service.impl;

import jakarta.annotation.PostConstruct;
import org.redisson.api.RBloomFilter;
import org.xjtu_learner.coffee_shop.common.utils.BloomFilterUtil;
import org.xjtu_learner.coffee_shop.entity.po.Shop;
import org.xjtu_learner.coffee_shop.dao.ShopMapper;
import org.xjtu_learner.coffee_shop.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.xjtu_learner.coffee_shop.common.utils.BloomFilterUtil.*;

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

    private RBloomFilter<String> bloomFilter;
    private final BloomFilterUtil bloomFilterUtil;

    public ShopServiceImpl(BloomFilterUtil bloomFilterUtil) {
        this.bloomFilterUtil = bloomFilterUtil;
    }

    @PostConstruct
    void init(){
        this.bloomFilter = bloomFilterUtil.getBloomFilter(BLOOMFILTER_SHOP_GOODS_RELATION, BLOOMFILTER_SHOP_GOODS_RELATION_SIZE, BLOOMFILTER_SHOP_GOODS_RELATION_FPP);
        // 初始化布隆过滤器
        List<String> shopIdList = lambdaQuery()
                .select(Shop::getId)
                .list().stream()
                .map(Shop::getId)
                .map(Object::toString)
                .toList();

        bloomFilter.add(shopIdList);
    }

    @Override
    public boolean checkShopIdValid(Integer shopId) {
        return bloomFilter.contains(String.valueOf(shopId));
    }

    @Override
    public long checkShopIdValidBatch(List<Integer> shopIdList) {
        List<String> list = shopIdList.stream().map(Object::toString).toList();
        return bloomFilter.contains(list);
    }

}

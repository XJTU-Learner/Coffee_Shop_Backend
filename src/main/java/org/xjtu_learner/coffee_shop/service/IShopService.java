package org.xjtu_learner.coffee_shop.service;

import org.redisson.api.RBloomFilter;
import org.xjtu_learner.coffee_shop.entity.po.Shop;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 门店表 服务类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-06
 */
public interface IShopService extends IService<Shop> {

    boolean checkShopIdValid(Integer shopId);

    long checkShopIdValidBatch(List<Integer> shopIdList);
}

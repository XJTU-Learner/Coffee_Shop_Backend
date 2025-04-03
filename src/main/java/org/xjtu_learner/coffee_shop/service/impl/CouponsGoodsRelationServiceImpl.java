package org.xjtu_learner.coffee_shop.service.impl;

import org.xjtu_learner.coffee_shop.entity.po.CouponsGoodsRelation;
import org.xjtu_learner.coffee_shop.dao.CouponsGoodsRelationMapper;
import org.xjtu_learner.coffee_shop.service.ICouponsGoodsRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 优惠卷使用规则对应的指定商品，如优惠卷用于‘美国拿铁’ 服务实现类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Service
public class CouponsGoodsRelationServiceImpl extends ServiceImpl<CouponsGoodsRelationMapper, CouponsGoodsRelation> implements ICouponsGoodsRelationService {

}

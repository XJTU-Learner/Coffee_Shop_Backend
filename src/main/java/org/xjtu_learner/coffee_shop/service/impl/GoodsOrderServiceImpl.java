package org.xjtu_learner.coffee_shop.service.impl;

import org.xjtu_learner.coffee_shop.entity.po.GoodsOrder;
import org.xjtu_learner.coffee_shop.dao.GoodsOrderMapper;
import org.xjtu_learner.coffee_shop.service.IGoodsOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Service
public class GoodsOrderServiceImpl extends ServiceImpl<GoodsOrderMapper, GoodsOrder> implements IGoodsOrderService {

}

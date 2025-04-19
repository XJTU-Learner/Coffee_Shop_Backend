package org.xjtu_learner.coffee_shop.service;

import org.xjtu_learner.coffee_shop.entity.form.GoodsOrderForm;
import org.xjtu_learner.coffee_shop.entity.po.GoodsOrder;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
public interface IGoodsOrderService extends IService<GoodsOrder> {

    Integer createOrder(GoodsOrderForm goodsOrderForm);

    boolean markOrderPaySuccess(Integer orderId);

    boolean timeoutCancel(Integer orderId);

    void payOrderByBalance(Integer orderId);
}

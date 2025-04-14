package org.xjtu_learner.coffee_shop.service.impl;

import org.xjtu_learner.coffee_shop.entity.dto.GoodsOrderForm;
import org.xjtu_learner.coffee_shop.entity.dto.MemberOrderForm;
import org.xjtu_learner.coffee_shop.entity.po.GoodsOrderDetail;
import org.xjtu_learner.coffee_shop.dao.GoodsOrderDetailMapper;
import org.xjtu_learner.coffee_shop.service.IGoodsOrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * <p>
 * 订单商品详情表 服务实现类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Service
public class GoodsOrderDetailServiceImpl extends ServiceImpl<GoodsOrderDetailMapper, GoodsOrderDetail> implements IGoodsOrderDetailService {


    public GoodsOrderDetail getByOrderId(int orderId){

        return lambdaQuery().eq(GoodsOrderDetail::getOrderId,orderId).one();
    }

    public void creatOrderDetail(MemberOrderForm memberOrderForm,int orderId) {
        for(GoodsOrderForm goodsOrderForm:memberOrderForm.getGoodsOrderFormList())
        {
            GoodsOrderDetail goodsOrderDetail =new GoodsOrderDetail();
            goodsOrderDetail.setOrderId(orderId);
            goodsOrderDetail.setGoodsId(goodsOrderForm.getGoods_id());
            goodsOrderDetail.setCount(goodsOrderDetail.getCount());
            goodsOrderDetail.setIsUsedCoupons(goodsOrderDetail.getIsUsedCoupons());
            save(goodsOrderDetail);

        }
    }
}

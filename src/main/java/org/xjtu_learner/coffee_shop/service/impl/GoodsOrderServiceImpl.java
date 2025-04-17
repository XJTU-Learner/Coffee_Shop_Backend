package org.xjtu_learner.coffee_shop.service.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.xjtu_learner.coffee_shop.common.auth.context.MemberContext;
import org.xjtu_learner.coffee_shop.common.enums.OrderStatus;
import org.xjtu_learner.coffee_shop.common.enums.PaymentStatus;
import org.xjtu_learner.coffee_shop.common.enums.PreferentialType;
import org.xjtu_learner.coffee_shop.common.exception.CommonException;
import org.xjtu_learner.coffee_shop.config.RabbitMQConfig;
import org.xjtu_learner.coffee_shop.entity.form.GoodsOrderForm;
import org.xjtu_learner.coffee_shop.entity.form.MemberOrderForm;
import org.xjtu_learner.coffee_shop.entity.po.*;
import org.xjtu_learner.coffee_shop.dao.GoodsOrderMapper;
import org.xjtu_learner.coffee_shop.service.IGoodsOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.NOT_EXIST;

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

    private  final  GoodsServiceImpl goodsService;
    private  final  GoodsOrderDetailServiceImpl goodsOrderDetailService;
    private  final  CouponsServiceImpl couponsService;
    private  final  CouponsMemberRelationServiceImpl couponsMemberRelationService ;

    private final RabbitTemplate rabbitTemplate;

    public GoodsOrderServiceImpl(GoodsServiceImpl goodsService, GoodsOrderDetailServiceImpl goodsOrderDetailService, CouponsServiceImpl couponsService, CouponsMemberRelationServiceImpl couponsMemberRelationService, RabbitTemplate rabbitTemplate) {
        this.goodsService = goodsService;
        this.goodsOrderDetailService = goodsOrderDetailService;
        this.couponsService = couponsService;
        this.couponsMemberRelationService = couponsMemberRelationService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void createOrder(MemberOrderForm memberOrderForm) {
        GoodsOrder goodsOrder=new GoodsOrder();
        goodsOrder.setMemberId(MemberContext.get().getId());
        goodsOrder.setMerchantId(memberOrderForm.getMerchant_id());
        goodsOrder.setCouponsMemberRelationId(memberOrderForm.getCoupons_member_relation_id());
        goodsOrder.setDescription(memberOrderForm.getDescription());
        goodsOrder.setRemark(memberOrderForm.getRemark());
        goodsOrder.setPaymentMode(memberOrderForm.getPayment_mode());

        Double Suposal_Price=0.0;
        Double Actual_Price=0.0;
        Double Perform_Extra_Fee=0.0;
        int count=0;

        //计算应付款和实际付款以及商家抽成比例
        for(GoodsOrderForm goodsOrderForm:memberOrderForm.getGoodsOrderFormList())
        {
            Suposal_Price+=goodsOrderForm.getActual_price()*goodsOrderForm.getCount();
            count+=goodsOrderForm.getCount();
            if(goodsOrderForm.getIs_used_coupons()){
                // 使用优惠券
                double temp=goodsOrderForm.getActual_price()*(getDiscountAmount(goodsOrderForm.getCoupons_id(),0.0).toBigInteger().doubleValue());
                double temp2=goodsOrderForm.getActual_price()*(goodsOrderForm.getCount()-1);
                Perform_Extra_Fee+=(temp+temp2)*(getPlatformExtractPrice(goodsOrderForm.getGoods_id()).toBigInteger().doubleValue());
                Actual_Price+=temp+temp2;

            }
            else {
                double temp=goodsOrderForm.getActual_price()*goodsOrderForm.getCount();
                Actual_Price+=temp;
                Perform_Extra_Fee+=temp*(getPlatformExtractPrice(goodsOrderForm.getGoods_id()).toBigInteger().doubleValue());
            }

        }
        //计算满减券
        Actual_Price-=getDiscountAmount(memberOrderForm.getCoupons_member_relation_id(),Actual_Price).toBigInteger().doubleValue();
        goodsOrder.setGoodsTotalPrice(new BigDecimal(Suposal_Price));
        goodsOrder.setActualPrice(new BigDecimal(Actual_Price));
        goodsOrder.setGoodsTotalQuantity(count);
        goodsOrder.setCouponDiscountPrice(new BigDecimal(Suposal_Price-Actual_Price));
        goodsOrder.setPointIncrease(BigDecimal.valueOf(Actual_Price));
        goodsOrder.setPlatformExtractPrice(new BigDecimal(Perform_Extra_Fee));
        goodsOrder.setMerchantIncome(new BigDecimal(Actual_Price-Perform_Extra_Fee));
        goodsOrder.setStatus(OrderStatus.WAIT_PAY);
        goodsOrder.setPaymentStatus(PaymentStatus.WaitPay);
        //创建用户订单
        save(goodsOrder);

        //创建订单详情
        goodsOrderDetailService.creatOrderDetail(memberOrderForm,goodsOrder.getId());
        //发送进入消息队列
        rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE,RabbitMQConfig.ORDER_ROUTING_KEY,goodsOrder.getId());

    }

    public BigDecimal getDiscountAmount(int MembercouponsId,double actual)
    {

        CouponsMemberRelation couponsMemberRelation =couponsMemberRelationService.getById(MembercouponsId);

        if(couponsMemberRelation==null)
        {
            return new BigDecimal(0);
        }
        else {

            int couponsId=couponsMemberRelation.getCouponsId();
            Coupons coupons=couponsService.getById(couponsId);
            if(coupons==null || coupons.getIsDeleted())
            {
                return new BigDecimal(0);
            }

            //更新优惠券状态为已使用
            couponsMemberRelation.setIsUsed(true);
            couponsMemberRelationService.updateById(couponsMemberRelation);
            if(coupons.getPreferentialType()== PreferentialType.DISCOUNT)
            {
                return coupons.getDiscountAmount();
            }
            else
            {

                return coupons.getLimitedPrice().toBigInteger().doubleValue()>actual?new BigDecimal(0.0):coupons.getReducedPrice();
            }
        }
    }


    //获取商品的平台抽成比例

    public BigDecimal getPlatformExtractPrice(int goodsId){
        Goods goods =goodsService.getById(goodsId);
        if (goods ==null)
        {
            throw  new CommonException("订单存在可疑可疑商品",NOT_EXIST);
        }
        else   {
            return goods.getPlatformExtractRatio();
        }

    }
}

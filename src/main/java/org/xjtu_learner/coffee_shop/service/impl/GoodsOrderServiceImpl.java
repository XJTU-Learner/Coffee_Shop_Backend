package org.xjtu_learner.coffee_shop.service.impl;


import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.xjtu_learner.coffee_shop.common.auth.context.MemberContext;
import org.xjtu_learner.coffee_shop.common.enums.OrderStatus;
import org.xjtu_learner.coffee_shop.common.enums.PaymentStatus;
import org.xjtu_learner.coffee_shop.common.enums.PreferentialType;
import org.xjtu_learner.coffee_shop.common.exception.CommonException;
import org.xjtu_learner.coffee_shop.entity.dto.GoodsOrderForm;
import org.xjtu_learner.coffee_shop.entity.dto.MemberOrderForm;
import org.xjtu_learner.coffee_shop.entity.dto.PayOrderForm;
import org.xjtu_learner.coffee_shop.entity.po.*;
import org.xjtu_learner.coffee_shop.dao.GoodsOrderMapper;
import org.xjtu_learner.coffee_shop.service.IGoodsOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.connection.CorrelationData;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.NOT_EXIST;
import static org.xjtu_learner.coffee_shop.common.constant.OrderMq.*;
import static org.xjtu_learner.coffee_shop.common.constant.PayMq.*;

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

    private static final Logger logger = LoggerFactory.getLogger(GoodsOrderServiceImpl.class);
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private  final  GoodsServiceImpl goodsService;
    private  final  GoodsOrderDetailServiceImpl goodsOrderDetailService;
    private  final  CouponsServiceImpl couponsService;
    private  final  CouponsMemberRelationServiceImpl couponsMemberRelationService ;
    private  final CallMeBotService callMeBotService;
    private final MemberServiceImpl memberService;

    private final RabbitTemplate rabbitTemplate;

    private final TransactionTemplate transactionTemplate;

    public GoodsOrderServiceImpl(GoodsServiceImpl goodsService, GoodsOrderDetailServiceImpl goodsOrderDetailService, CouponsServiceImpl couponsService, CouponsMemberRelationServiceImpl couponsMemberRelationService, CallMeBotService callMeBotService, MemberServiceImpl memberService, RabbitTemplate rabbitTemplate, TransactionTemplate transactionTemplate) {
        this.goodsService = goodsService;
        this.goodsOrderDetailService = goodsOrderDetailService;
        this.couponsService = couponsService;
        this.couponsMemberRelationService = couponsMemberRelationService;
        this.callMeBotService = callMeBotService;
        this.memberService = memberService;
        this.rabbitTemplate = rabbitTemplate;
        this.transactionTemplate = transactionTemplate;
    }


    //创建订单
    @Override
    public void createOrder(MemberOrderForm memberOrderForm) {
        // 使用TransactionTemplate执行事务并返回订单对象
        GoodsOrder goodsOrder = transactionTemplate.execute(status -> {
            try {
                GoodsOrder order = new GoodsOrder();
                order.setMemberId(MemberContext.get().getId());
                order.setMerchantId(memberOrderForm.getMerchant_id());
                order.setCouponsMemberRelationId(memberOrderForm.getCoupons_member_relation_id());
                order.setDescription(memberOrderForm.getDescription());
                order.setRemark(memberOrderForm.getRemark());
                order.setPaymentMode(memberOrderForm.getPayment_mode());

                Double supposalPrice = 0.0;
                Double actualPrice = 0.0;
                Double performExtraFee = 0.0;
                int count = 0;
                List<GoodsOrderForm> goodsOrderFormList = memberOrderForm.getGoodsOrderFormList();
                for (GoodsOrderForm goodsOrderForm : goodsOrderFormList) {
                    supposalPrice += goodsOrderForm.getActual_price() * goodsOrderForm.getCount();
                    count += goodsOrderForm.getCount();
                    if (goodsOrderForm.getIs_used_coupons()) {
                        double temp = goodsOrderForm.getActual_price() *
                                getDiscountAmount(goodsOrderForm.getCoupons_id(), 0.0).toBigInteger().doubleValue();
                        double temp2 = goodsOrderForm.getActual_price() * (goodsOrderForm.getCount() - 1);
                        performExtraFee += (temp + temp2) *
                                getPlatformExtractPrice(goodsOrderForm.getGoods_id()).toBigInteger().doubleValue();
                        actualPrice += temp + temp2;
                    } else {
                        double temp = goodsOrderForm.getActual_price() * goodsOrderForm.getCount();
                        actualPrice += temp;
                        performExtraFee += temp *
                                getPlatformExtractPrice(goodsOrderForm.getGoods_id()).toBigInteger().doubleValue();
                    }
                }
                actualPrice -= getDiscountAmount(memberOrderForm.getCoupons_member_relation_id(), actualPrice)
                        .toBigInteger().doubleValue();
                order.setGoodsTotalPrice(new BigDecimal(supposalPrice));
                order.setActualPrice(new BigDecimal(actualPrice));
                order.setGoodsTotalQuantity(count);
                order.setCouponDiscountPrice(new BigDecimal(supposalPrice - actualPrice));
                order.setPointIncrease(BigDecimal.valueOf(actualPrice));
                order.setPlatformExtractPrice(new BigDecimal(performExtraFee));
                order.setMerchantIncome(new BigDecimal(actualPrice - performExtraFee));
                order.setStatus(OrderStatus.WAIT_PAY);
                order.setPaymentStatus(PaymentStatus.WaitPay);

                save(order);
                goodsOrderDetailService.creatOrderDetail(memberOrderForm, order.getId());

                //订单成功创建通知
                Member member =memberService.getById(MemberContext.get().getId());
                String message="您的订单:"+order.getId()+order.getDescription()+"已经成功下单，请在两分钟支付！";
                callMeBotService.sendMessage(member.getTelegram(),message);
                return order;
            } catch (Exception e) {
                status.setRollbackOnly();
                throw new RuntimeException("订单创建失败", e);
            }
        });

        // 在事务之外使用返回的订单对象
        if (goodsOrder != null) {
           sendOrderMessage(goodsOrder);
        }
    }


    //支付订单
    @Override
    public void payOrder(Member member,PayOrderForm payOrderForm) {

        //执行扣费
        member.setBalance(member.getBalance().subtract(new BigDecimal(payOrderForm.getActual_price())));
        member.setTotalConsumeBalance(new BigDecimal(payOrderForm.getActual_price()));
        member.setTotalConsumePoints(new BigDecimal(payOrderForm.getActual_price()));
        member.setPoints(member.getPoints().add(new BigDecimal(payOrderForm.getActual_price())));
        //加入消息队列执行异步消费
       sendPayMessage(payOrderForm.getOrder_id());
    }

    /*
    *
    *
    * 辅助函数
    *
    * */



    //  将订单信息发送到消息队列
    private void sendOrderMessage(GoodsOrder goodsOrder) {
        int orderId = goodsOrder.getId();
        CorrelationData correlationData = new CorrelationData(String.valueOf(orderId));
        AtomicInteger retryCount = new AtomicInteger(0);

        // 设置确认确认进入交换机回调
        rabbitTemplate.setConfirmCallback((data, ack, cause) -> {
            if (data == null) {
                logger.error( "CorrelationData is null in confirm callback");
                return;
            }
            String messageId = data.getId();
            if (ack) {
                logger.info("Message sent successfully for orderId: {}", messageId);
            } else {
                logger.error("Message send failed for orderId: {}, cause: {}", messageId, cause);
                if (retryCount.getAndIncrement() < MAX_RETRY_ATTEMPTS) {
                    logger.info("Retrying send for orderId: {}, attempt: {}", messageId, retryCount.get());
                    rabbitTemplate.convertAndSend(ORDER_EXCHANGE, ORDER_ROUTING_KEY, orderId, data);
                } else {
                    logger.error("Max retry attempts reached for orderId: {}", messageId);
                    // 可选：记录到数据库或死信队列
                }
            }
        });

        // 确认进入队列返回回调
        rabbitTemplate.setReturnsCallback(returned -> {
            String messageId = returned.getMessage().getMessageProperties().getCorrelationId();
            logger.error("Message returned for orderId: {}, reason: {}, exchange: {}, routingKey: {}",
                    messageId, returned.getReplyText(), returned.getExchange(), returned.getRoutingKey());
        });
        // 发送消息
        logger.info("Attempting to send message for orderId: {}", orderId);
        rabbitTemplate.convertAndSend(ORDER_EXCHANGE, ORDER_ROUTING_KEY, orderId, correlationData);
    }

    //将支付消息发送到消息队列

    private void sendPayMessage(int orderId) {
        CorrelationData correlationData = new CorrelationData(String.valueOf(orderId));
        AtomicInteger retryCount =new AtomicInteger(0);
        rabbitTemplate.setConfirmCallback((data, ack, cause) -> {
            if (data == null) {
                logger.error( "CorrelationData is null in confirm callback!");
                return;
            }
            String messageId = data.getId();
            if (ack) {
                logger.info("Message sent successfully for payOrderId: {}", messageId);
            } else {
                logger.error("Message send failed for payOrderId: {}, cause: {}", messageId, cause);
                if (retryCount.getAndIncrement() < MAX_RETRY_ATTEMPTS) {
                    logger.info("Retrying send for payOrderId: {}, attempt: {}", messageId, retryCount.get());
                    rabbitTemplate.convertAndSend(PAY_EXCHANGE, PAY_ROUTING_KEY, orderId, data);
                } else {
                    logger.error("Max retry attempts reached for payOrderId: {}", messageId);
                }
            }
        });

        // 确认进入队列返回回调
        rabbitTemplate.setReturnsCallback(returned -> {
            String messageId = returned.getMessage().getMessageProperties().getCorrelationId();
            logger.error("Message returned for payOrderId: {}, reason: {}, exchange: {}, routingKey: {}",
                    messageId, returned.getReplyText(), returned.getExchange(), returned.getRoutingKey());
        });

        rabbitTemplate.convertAndSend(PAY_EXCHANGE,PAY_ROUTING_KEY,orderId);

    }

    @Transactional
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
            if(coupons==null || coupons.getIsDelete())
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

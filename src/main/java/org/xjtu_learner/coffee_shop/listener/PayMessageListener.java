package org.xjtu_learner.coffee_shop.listener;


import jakarta.validation.Valid;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.xjtu_learner.coffee_shop.common.auth.context.MemberContext;
import org.xjtu_learner.coffee_shop.common.enums.OrderStatus;
import org.xjtu_learner.coffee_shop.common.enums.PaymentStatus;
import org.xjtu_learner.coffee_shop.entity.dto.PayOrderForm;
import org.xjtu_learner.coffee_shop.entity.po.GoodsOrder;
import org.xjtu_learner.coffee_shop.entity.po.GoodsOrderDetail;
import org.xjtu_learner.coffee_shop.entity.po.Member;
import org.xjtu_learner.coffee_shop.service.impl.CallMeBotService;
import org.xjtu_learner.coffee_shop.service.impl.GoodsOrderDetailServiceImpl;
import org.xjtu_learner.coffee_shop.service.impl.GoodsOrderServiceImpl;
import org.xjtu_learner.coffee_shop.service.impl.MemberServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.xjtu_learner.coffee_shop.common.constant.PayMq.PAY_QUEUE;
@Component
@Validated
public class PayMessageListener {

    private final MemberServiceImpl memberService;
    private final GoodsOrderServiceImpl goodsOrderService;
    private final GoodsOrderDetailServiceImpl goodsOrderDetailService;
    private final CallMeBotService callMeBotService;

    public PayMessageListener(MemberServiceImpl memberService, GoodsOrderServiceImpl goodsOrderService, GoodsOrderDetailServiceImpl goodsOrderDetailService, CallMeBotService callMeBotService) {
        this.memberService = memberService;
        this.goodsOrderService = goodsOrderService;
        this.goodsOrderDetailService = goodsOrderDetailService;
        this.callMeBotService = callMeBotService;
    }

    @Transactional
    @RabbitListener(queues = PAY_QUEUE)
    public void handlePay(@Payload @Valid int OrderId) {
        try {
            // 更新订单状态
            GoodsOrder goodsOrder=goodsOrderService.getById(OrderId);
            Member member=memberService.getById(goodsOrder.getMemberId());
            goodsOrder.setPaymentStatus( PaymentStatus.Success);
            goodsOrder.setPaymentSuccessTime(LocalDateTime.now());
            goodsOrder.setUpdateAt(LocalDateTime.now());
            goodsOrderService.updateById(goodsOrder);

            //通知用户
            String message="您的订单:"+OrderId+goodsOrder.getDescription()+"已经成功下单，请耐心等待商家接单制作！";
            callMeBotService.sendMessage(member.getTelegram(),message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

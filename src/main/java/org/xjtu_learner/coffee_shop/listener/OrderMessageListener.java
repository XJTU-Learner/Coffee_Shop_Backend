package org.xjtu_learner.coffee_shop.listener;


import org.mybatis.spring.MyBatisSystemException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.xjtu_learner.coffee_shop.common.enums.OrderStatus;
import org.xjtu_learner.coffee_shop.common.enums.PaymentStatus;
import org.xjtu_learner.coffee_shop.config.RabbitMQConfig;
import org.xjtu_learner.coffee_shop.entity.po.GoodsOrder;
import org.xjtu_learner.coffee_shop.entity.po.GoodsOrderDetail;
import org.xjtu_learner.coffee_shop.entity.po.Member;
import org.xjtu_learner.coffee_shop.service.impl.CallMeBotService;
import org.xjtu_learner.coffee_shop.service.impl.GoodsOrderDetailServiceImpl;
import org.xjtu_learner.coffee_shop.service.impl.GoodsOrderServiceImpl;
import org.xjtu_learner.coffee_shop.service.impl.MemberServiceImpl;

import static org.xjtu_learner.coffee_shop.common.constant.OrderMq.ORDER_DLX_QUEUE;

@Component
public class OrderMessageListener {

    private final GoodsOrderServiceImpl goodsOrderService;
    private final MemberServiceImpl memberService;
    private final CallMeBotService callMeBotService;
    private final GoodsOrderDetailServiceImpl goodsOrderDetailService;

    public OrderMessageListener(GoodsOrderServiceImpl goodsOrderService, MemberServiceImpl memberService, CallMeBotService callMeBotService, GoodsOrderDetailServiceImpl goodsOrderDetailService) {
        this.goodsOrderService = goodsOrderService;
        this.memberService = memberService;
        this.callMeBotService = callMeBotService;
        this.goodsOrderDetailService = goodsOrderDetailService;
    }


    @RabbitListener(queues = ORDER_DLX_QUEUE)
    @Transactional
    public void handleExpireOrder(int orderId){
        //获取对象
        GoodsOrder goodsOrder =goodsOrderService.getById(orderId);
        //判读是否支付
        if(goodsOrder!=null && goodsOrder.getPaymentStatus()== PaymentStatus.WaitPay)
        {

            try {
                //修改订单表中订单的状态，置为失败
                goodsOrder.setPaymentStatus(PaymentStatus.OverTime);
                goodsOrder.setStatus(OrderStatus.Cancel);
                goodsOrderService.updateById(goodsOrder);

                //通知用户
                Member member=memberService.getById(goodsOrder.getMemberId());
                String message="您的订单:"+orderId+goodsOrder.getDescription()+"已经超时未支付，请重新下单！";
                callMeBotService.sendMessage(member.getTelegram(),message);

            }
            catch (Exception e){
                System.err.println("Database error for order " + orderId + ": " + e.getMessage());
                e.printStackTrace();
            }

        }
    }
}

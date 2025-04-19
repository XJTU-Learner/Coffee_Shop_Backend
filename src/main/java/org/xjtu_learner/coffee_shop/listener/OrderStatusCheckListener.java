package org.xjtu_learner.coffee_shop.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.xjtu_learner.coffee_shop.common.constant.MqConstant;
import org.xjtu_learner.coffee_shop.common.enums.OrderStatus;
import org.xjtu_learner.coffee_shop.common.enums.PaymentStatus;
import org.xjtu_learner.coffee_shop.common.exception.CommonException;
import org.xjtu_learner.coffee_shop.common.mq.MultiDelayMessage;
import org.xjtu_learner.coffee_shop.common.mq.processor.DelayMessageProcessor;
import org.xjtu_learner.coffee_shop.entity.po.GoodsOrder;
import org.xjtu_learner.coffee_shop.service.IGoodsOrderService;

import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.UPDATE_FAILED;

@Slf4j
@Component
public class OrderStatusCheckListener {

    private final RabbitTemplate rabbitTemplate;
    private final IGoodsOrderService goodsOrderService;

    public OrderStatusCheckListener(RabbitTemplate rabbitTemplate, IGoodsOrderService goodsOrderService) {
        this.rabbitTemplate = rabbitTemplate;
        this.goodsOrderService = goodsOrderService;
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConstant.ORDER_DELAY_QUEUE, durable = "true"),
            exchange = @Exchange(value = MqConstant.ORDER_DELAY_EXCHANGE, delayed = "true", type = ExchangeTypes.TOPIC),
            key = MqConstant.ORDER_DELAY_ROUTING_KEY
    ))
    public void listenOrderDelayMessage(MultiDelayMessage<Long> msg) {
        GoodsOrder order = goodsOrderService.getById(msg.getData());

        // 订单不存在或者已经被处理（即不是未支付状态）
        if(order == null || order.getStatus() != OrderStatus.UNPAID){
            return;
        }
        Integer orderId = order.getId();
        // 如果支付状态为已支付，但订单状态仍为未付款，则修改订单状态并入库
        if(order.getPaymentStatus() == PaymentStatus.PAID){
            boolean success = goodsOrderService.markOrderPaySuccess(orderId);
            if(!success){
                throw new CommonException("更新订单状态失败",UPDATE_FAILED);
            }

            //TODO: 向用户推送下单成功消息
            log.debug("向用户推送下单成功消息");


            //TODO: 向商家客户端推送新订单
            log.debug("向商家客户端推送新订单");
        }

        // 如果支付状态仍为未支付，则再次发送延迟消息
        if(msg.hasNextDelay()){
            Long nextDelay = msg.removeNextDelay();
            rabbitTemplate.convertAndSend(
                    MqConstant.ORDER_DELAY_EXCHANGE,MqConstant.ORDER_DELAY_ROUTING_KEY, msg,
                    new DelayMessageProcessor(nextDelay)
            );
            log.debug("再次发送延迟消息:{}", nextDelay);
            return;
        }

        // 重复机会用尽，将订单状态置为超时取消
        boolean success = goodsOrderService.timeoutCancel(orderId);
        if(!success){
            throw new CommonException("更新订单状态失败",UPDATE_FAILED);
        }
    }
}

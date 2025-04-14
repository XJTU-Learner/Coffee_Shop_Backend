package org.xjtu_learner.coffee_shop.listener;


import org.mybatis.spring.MyBatisSystemException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.xjtu_learner.coffee_shop.common.enums.OrderStatus;
import org.xjtu_learner.coffee_shop.common.enums.PaymentStatus;
import org.xjtu_learner.coffee_shop.config.RabbitMQConfig;
import org.xjtu_learner.coffee_shop.entity.po.GoodsOrder;
import org.xjtu_learner.coffee_shop.entity.po.GoodsOrderDetail;
import org.xjtu_learner.coffee_shop.service.impl.GoodsOrderDetailServiceImpl;
import org.xjtu_learner.coffee_shop.service.impl.GoodsOrderServiceImpl;

@Component
public class OrderMessageListener {

    private final GoodsOrderServiceImpl goodsOrderService;

    private final GoodsOrderDetailServiceImpl goodsOrderDetailService;

    public OrderMessageListener(GoodsOrderServiceImpl goodsOrderService, GoodsOrderDetailServiceImpl goodsOrderDetailService) {
        this.goodsOrderService = goodsOrderService;
        this.goodsOrderDetailService = goodsOrderDetailService;
    }


    @RabbitListener(queues = RabbitMQConfig.ORDER_DLX_QUEUE)
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
                System.out.println("已经消费:"+orderId);
//                //逻辑删除订单详情表中的相应orderid 的订单
//
//                GoodsOrderDetail goodsOrderDetail = goodsOrderDetailService.getByOrderId(orderId);
//                goodsOrderDetail.setIsDeleted(true);
//                goodsOrderDetailService.updateById(goodsOrderDetail);

            }
            catch (MyBatisSystemException e){
                System.err.println("Database error for order " + orderId + ": " + e.getMessage());
                e.printStackTrace();
            }

        }
    }
}

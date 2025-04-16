package org.xjtu_learner.coffee_shop.common.constant;

public class OrderMq {

    public static final String ORDER_QUEUE = "order.queue";
    public static final String ORDER_DLX_QUEUE = "order.dlx.queue";
    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String ORDER_DLX_EXCHANGE = "order.dlx.exchange";
    public static final String ORDER_ROUTING_KEY = "order.create";
    public static final String ORDER_DLX_ROUTING_KEY = "order.dlx";
}

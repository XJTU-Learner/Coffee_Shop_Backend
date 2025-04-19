package org.xjtu_learner.coffee_shop.common.constant;

public class MqConstant {

    public static final String ORDER_QUEUE = "order.queue";
    public static final String ORDER_DLX_QUEUE = "order.dlx.queue";
    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String ORDER_DLX_EXCHANGE = "order.dlx.exchange";
    public static final String ORDER_ROUTING_KEY = "order.create";
    public static final String ORDER_DLX_ROUTING_KEY = "order.dlx";

    public static final String ORDER_DELAY_EXCHANGE = "order.delay.topic";
    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";
    public static final String ORDER_DELAY_ROUTING_KEY = "order.query";

    public static final String PAY_QUEUE = "balance.queue";
    public static final String PAY_EXCHANGE = "balance.exchange";
    public static final String PAY_ROUTING_KEY = "balance.deduct";
}

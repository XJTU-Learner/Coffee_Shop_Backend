package org.xjtu_learner.coffee_shop.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ORDER_QUEUE = "order.queue";
    public static final String ORDER_DLX_QUEUE = "order.dlx.queue";
    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String ORDER_DLX_EXCHANGE = "order.dlx.exchange";
    public static final String ORDER_ROUTING_KEY = "order.create";
    public static final String ORDER_DLX_ROUTING_KEY = "order.dlx";

    // 正常队列（设置死信交换机和路由键）
    @Bean
    public Queue orderQueue() {
        return QueueBuilder.durable(ORDER_QUEUE)
                .withArgument("x-dead-letter-exchange", ORDER_DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", ORDER_DLX_ROUTING_KEY)
                .withArgument("x-message-ttl", 60000) // 5 分钟 TTL（毫秒）
                .build();
    }

    // 死信队列
    @Bean
    public Queue orderDlxQueue() {
        return new Queue(ORDER_DLX_QUEUE, true);
    }

    // 正常交换机
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE);
    }

    // 死信交换机
    @Bean
    public DirectExchange orderDlxExchange() {
        return new DirectExchange(ORDER_DLX_EXCHANGE);
    }

    // 绑定正常队列
    @Bean
    public Binding orderBinding(Queue orderQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderQueue).to(orderExchange).with(ORDER_ROUTING_KEY);
    }

    // 绑定死信队列
    @Bean
    public Binding orderDlxBinding(Queue orderDlxQueue, DirectExchange orderDlxExchange) {
        return BindingBuilder.bind(orderDlxQueue).to(orderDlxExchange).with(ORDER_DLX_ROUTING_KEY);
    }
}

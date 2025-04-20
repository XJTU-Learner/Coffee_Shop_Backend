package org.xjtu_learner.coffee_shop.config;

import org.apache.catalina.User;
import org.springframework.amqp.core.*;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.xjtu_learner.coffee_shop.common.constant.OrderMq.*;
import static org.xjtu_learner.coffee_shop.common.constant.PayMq.*;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        rabbitTemplate.setMandatory(true); // 启用强制投递
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter jsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        return factory;
    }

   /*
    * 创建订单队列
    */
    @Bean
    public Queue orderQueue() {
        return QueueBuilder.durable(ORDER_QUEUE)
                .withArgument("x-dead-letter-exchange", ORDER_DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", ORDER_DLX_ROUTING_KEY)
                .withArgument("x-message-ttl", 120000) // 5 分钟 TTL（毫秒）
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



    /*
    * 订单支付队列
    *
    */

    // 余额扣减队列（不配置死信）
    @Bean
    public Queue balanceQueue() {
        return QueueBuilder.durable(PAY_QUEUE).build();
    }

    // 余额扣减交换机
    @Bean
    public DirectExchange balanceExchange() {
        return new DirectExchange(PAY_EXCHANGE);
    }

    // 绑定队列到交换机
    @Bean
    public Binding balanceBinding(Queue balanceQueue, DirectExchange balanceExchange) {
        return BindingBuilder.bind(balanceQueue).to(balanceExchange).with(PAY_ROUTING_KEY);
    }



}

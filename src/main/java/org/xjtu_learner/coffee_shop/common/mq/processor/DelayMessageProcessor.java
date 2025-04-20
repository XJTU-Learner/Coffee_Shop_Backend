package org.xjtu_learner.coffee_shop.common.mq.processor;


import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

public class DelayMessageProcessor implements MessagePostProcessor {

    private final Long delay;

    public DelayMessageProcessor(Long delay) {
        this.delay = delay;
    }

    @Override
    public Message postProcessMessage(Message message) throws AmqpException {
        message.getMessageProperties().setDelayLong(delay);
        return message;
    }
}

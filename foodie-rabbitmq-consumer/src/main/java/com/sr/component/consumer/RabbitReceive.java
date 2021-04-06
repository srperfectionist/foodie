package com.sr.component.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author shirui
 * @date 2021/3/30
 */
@Component
@Slf4j
public class RabbitReceive {

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "queue-1", durable = "true"),
                                                            exchange = @Exchange(name = "exchange-1",
                                                                                    durable = "true",
                                                                                    type = "topic",
                                                                                    ignoreDeclarationExceptions = "true"),
                                                            key = "springboot.*"
    ))
    @RabbitHandler
    public void onMessage(Message<Object> message, Channel channel) throws IOException {
        log.info("消费消息:{}", message.getPayload());
        Long deliveryTag = (Long)message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);

        channel.basicAck(deliveryTag, false);
    }
}

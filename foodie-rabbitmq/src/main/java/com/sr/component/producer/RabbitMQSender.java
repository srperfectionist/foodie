package com.sr.component.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * @author shirui
 * @date 2021/3/29
 */
@Component
@Slf4j
public class RabbitMQSender {

    private RabbitTemplate rabbitTemplate;

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 确认消息的回调监听接口，用于确认消息是否被broker所收到
     */
    final RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {

        /**
         *
         * @param correlationData   唯一标识
         * @param ack               是否落盘成功
         * @param cause             失败的一些异常信息
         */
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            log.info("消息ACK结果:{}, correlationData:{}", ack, correlationData.getId());
        }
    };

    /**
     * 发送消息
     *
     * @param message       具体的消息内容
     * @param properties    额外的附加属性
     */
    public void send(Object message, Map<String, Object> properties){
        MessageHeaders messageHeaders = new MessageHeaders(properties);
        Message<Object> msg = MessageBuilder.createMessage(message, messageHeaders);
        rabbitTemplate.setConfirmCallback(confirmCallback);
        // 指定业务唯一ID
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
            @Override
            public org.springframework.amqp.core.Message postProcessMessage(org.springframework.amqp.core.Message message) throws AmqpException {
                log.info("post to do:{}", message);
                return message;
            }
        };
        /**
         * exchange,routingKey,message,
         */
        rabbitTemplate.convertAndSend("exchange-1",
                "springboot.rabbit",
                msg,
                messagePostProcessor,
                correlationData);
    }
}

package com.sr.rabbit.test;

import com.google.common.collect.Maps;
import com.sr.component.producer.RabbitMQSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

/**
 * @author shirui
 * @date 2021/4/5
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitMQTest {

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Test
    public void testSender() throws InterruptedException {
        Map<String, Object> properties = Maps.newHashMap();
        properties.put("attr1", "123456");
        properties.put("attr2", "123456");

        rabbitMQSender.send("hello rabbitmq", properties);

        Thread.sleep(10000);
    }
}

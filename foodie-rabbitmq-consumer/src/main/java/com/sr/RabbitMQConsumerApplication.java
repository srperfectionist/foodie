package com.sr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author shirui
 * @date 2021/3/29
 */
@SpringBootApplication
public class RabbitMQConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitMQConsumerApplication.class, args);
    }
}

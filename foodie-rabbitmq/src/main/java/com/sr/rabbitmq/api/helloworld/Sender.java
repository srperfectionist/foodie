package com.sr.rabbitmq.api.helloworld;

import com.google.common.collect.Maps;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author shirui
 * @date 2021/2/1
 */
@Slf4j
public class Sender {

    public static void main(String[] args) throws IOException, TimeoutException {
        exchangeQueueTTL();
    }

    /**
     * 不使用exchange
     *
     * @throws IOException
     * @throws TimeoutException
     */
    public static void queue() throws IOException, TimeoutException {
        // 1 创建ConnectionFactory
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("172.16.57.153");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        // 2 创建Connection
        Connection connection = connectionFactory.newConnection();

        // 3 创建Channel
        Channel channel = connection.createChannel();

        // 4 声明队列
        String queueName = "test";

        // 参数: queueName,是否持久化,独占的queue(仅供此链接),不使用时是否自动删除,其他参数
        /* 声明（创建）队列  queueDeclare( String queue, boolean durable, boolean exclusive, boolean autoDelete,  Map<String, Object> arguments)
         * queue - 队列名
         * durable - 是否是持久化队列， 队列的声明默认是存放到内存中的，如果rabbitmq重启会丢失
         * exclusie - 是否排外的，仅限于当前队列使用
         * autoDelete - 是否自动删除队列，当最后一个消费者断开连接之后队列是否自动被删除，可以通过界面 查看某个队列的消费者数量，当consumers = 0时队列就会自动删除
         * arguments - 队列携带的参数 比如 ttl-生命周期，x-dead-letter 死信队列等等
         */
        channel.queueDeclare(queueName, false, false, false, Maps.newHashMap());

        Map<String, Object> headers = Maps.newHashMap();

        // deliveryMode 1:不持久化 2:持久化
        AMQP.BasicProperties props = new AMQP.BasicProperties().builder()
                .deliveryMode(2)
                .contentEncoding("UTF-8")
                .headers(headers)
                .build();

        for (int i = 0; i < 5; i++){
            String msg = "Hello World RabbitMQ" + i;
            // 参数 exchange routingKey
            // 不指定exchange,默认走AMQP default
            // 非持久化 服务器重启就没有了
            channel.basicPublish("", "test", props, msg.getBytes());
        }
    }

    /**
     * 使用exchange 使用消息确认机制
     *
     * @throws IOException
     * @throws TimeoutException
     */
    public static void exchangeQueueConfirm() throws IOException, TimeoutException {
        // 1 创建ConnectionFactory
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("172.16.57.153");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        // 2 创建Connection
        Connection connection = connectionFactory.newConnection();

        // 3 创建Channel
        Channel channel = connection.createChannel();

        // 4 声明exchange
        channel.exchangeDeclare("exchange-test", BuiltinExchangeType.TOPIC, true, false, false, Maps.newHashMap());

        // 5 声明队列
        //String queueName = "queue-test";

        // 参数: queueName,是否持久化,独占的queue(仅供此链接),不使用时是否自动删除,其他参数
        /* 声明（创建）队列  queueDeclare( String queue, boolean durable, boolean exclusive, boolean autoDelete,  Map<String, Object> arguments)
         * queue - 队列名
         * durable - 是否是持久化队列， 队列的声明默认是存放到内存中的，如果rabbitmq重启会丢失
         * exclusie - 是否排外的，仅限于当前队列使用
         * autoDelete - 是否自动删除队列，当最后一个消费者断开连接之后队列是否自动被删除，可以通过界面 查看某个队列的消费者数量，当consumers = 0时队列就会自动删除
         * arguments - 队列携带的参数 比如 ttl-生命周期，x-dead-letter 死信队列等等
         */
        //channel.queueDeclare(queueName, true, false, false, Maps.newHashMap());

        // 6 启动confirm
        channel.confirmSelect();

        // 7 设置监听 异步
        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long l, boolean b) throws IOException {
                log.info("======ok======");
            }

            @Override
            public void handleNack(long l, boolean b) throws IOException {
                log.info("======error======");
            }
        });

        // deliveryMode 1:不持久化 2:持久化
        AMQP.BasicProperties props = new AMQP.BasicProperties().builder()
                .deliveryMode(2)
                .contentEncoding("UTF-8")
                .build();

        for (int i = 0; i < 5; i++){
            String msg = "Hello World RabbitMQ" + i;
            // 参数 exchange
            // 不指定exchange,默认走AMQP default
            // 非持久化 服务器重启就没有了
            channel.basicPublish("exchange-test", "test." + i,  props, msg.getBytes());
        }
    }

    /**
     * 消息队列 使用返回机制
     *
     * @throws IOException
     * @throws TimeoutException
     */
    public static void exchangeQueueReturn() throws IOException, TimeoutException {
        // 1 创建ConnectionFactory
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("172.16.57.153");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        // 2 创建Connection
        Connection connection = connectionFactory.newConnection();

        // 3 创建Channel
        Channel channel = connection.createChannel();

        // 4 声明exchange
        channel.exchangeDeclare("exchange-test", BuiltinExchangeType.TOPIC, true, false, false, Maps.newHashMap());

        // 5 声明队列
        //String queueName = "queue-test";

        // 参数: queueName,是否持久化,独占的queue(仅供此链接),不使用时是否自动删除,其他参数
        /* 声明（创建）队列  queueDeclare( String queue, boolean durable, boolean exclusive, boolean autoDelete,  Map<String, Object> arguments)
         * queue - 队列名
         * durable - 是否是持久化队列， 队列的声明默认是存放到内存中的，如果rabbitmq重启会丢失
         * exclusie - 是否排外的，仅限于当前队列使用
         * autoDelete - 是否自动删除队列，当最后一个消费者断开连接之后队列是否自动被删除，可以通过界面 查看某个队列的消费者数量，当consumers = 0时队列就会自动删除
         * arguments - 队列携带的参数 比如 ttl-生命周期，x-dead-letter 死信队列等等
         */
        //channel.queueDeclare(queueName, true, false, false, Maps.newHashMap());

        // 6 启动confirm
        channel.confirmSelect();

        // 7 设置监听 异步
        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long l, boolean b) throws IOException {
                log.info("======ok======");
            }

            @Override
            public void handleNack(long l, boolean b) throws IOException {
                log.info("======error======");
            }
        });

        // 8 启动返回机制 成功不打印日志 发送失败打印日志
        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int i, String s, String s1, String s2, AMQP.BasicProperties basicProperties, byte[] bytes) throws IOException {
                log.info("=====handleReturn=====");
                log.info("replyCode:{}", i);
                log.info("replyText:{}", s);
                log.info("exchange:{}", s1);
                log.info("routingKey:{}", s2);
                log.info("body:{}", new String(bytes));
            }
        });

        // deliveryMode 1:不持久化 2:持久化
        AMQP.BasicProperties props = new AMQP.BasicProperties().builder()
                .deliveryMode(2)
                .contentEncoding("UTF-8")
                .build();

        for (int i = 0; i < 5; i++){
            String msg = "Hello World RabbitMQ" + i;

            // 参数 exchange
            // 不指定exchange,默认走AMQP default
            // 非持久化 服务器重启就没有了
            channel.basicPublish("exchange-test", "abc." + i, true,  props, msg.getBytes());
        }
    }

    /**
     * Ack
     *
     * @throws IOException
     * @throws TimeoutException
     */
    public static void exchangeQueueAckOrNack() throws IOException, TimeoutException {
        // 1 创建ConnectionFactory
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("172.16.57.153");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        // 2 创建Connection
        Connection connection = connectionFactory.newConnection();

        // 3 创建Channel
        Channel channel = connection.createChannel();

        // 4 声明exchange
        channel.exchangeDeclare("exchange-test", BuiltinExchangeType.TOPIC, true, false, false, Maps.newHashMap());

        // 5 声明队列
        //String queueName = "queue-test";

        // 参数: queueName,是否持久化,独占的queue(仅供此链接),不使用时是否自动删除,其他参数
        /* 声明（创建）队列  queueDeclare( String queue, boolean durable, boolean exclusive, boolean autoDelete,  Map<String, Object> arguments)
         * queue - 队列名
         * durable - 是否是持久化队列， 队列的声明默认是存放到内存中的，如果rabbitmq重启会丢失
         * exclusie - 是否排外的，仅限于当前队列使用
         * autoDelete - 是否自动删除队列，当最后一个消费者断开连接之后队列是否自动被删除，可以通过界面 查看某个队列的消费者数量，当consumers = 0时队列就会自动删除
         * arguments - 队列携带的参数 比如 ttl-生命周期，x-dead-letter 死信队列等等
         */
        //channel.queueDeclare(queueName, true, false, false, Maps.newHashMap());

        for (int i = 0; i < 5; i++){
            String msg = "Hello World RabbitMQ" + i;

            Map<String, Object> headers = Maps.newHashMap();
            headers.put("flag", i);
            // deliveryMode 1:不持久化 2:持久化
            AMQP.BasicProperties props = new AMQP.BasicProperties().builder()
                    .deliveryMode(2)
                    .contentEncoding("UTF-8")
                    .headers(headers)
                    .build();

            // 参数 exchange
            // 不指定exchange,默认走AMQP default
            // 非持久化 服务器重启就没有了
            channel.basicPublish("exchange-test", "test." + i, true, props, msg.getBytes());
        }
    }

    /**
     * Ack
     *
     * @throws IOException
     * @throws TimeoutException
     */
    public static void exchangeQueueTTL() throws IOException, TimeoutException {
        // 1 创建ConnectionFactory
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("172.16.57.153");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        // 2 创建Connection
        Connection connection = connectionFactory.newConnection();

        // 3 创建Channel
        Channel channel = connection.createChannel();

        // 4 声明exchange
        String exchangeName = "test_dlx_exchange";
        String routingKey = "group.bfxy";
       // channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC, true, false, false, Maps.newHashMap());

        // 5 声明队列
        //String queueName = "queue-test";

        // 参数: queueName,是否持久化,独占的queue(仅供此链接),不使用时是否自动删除,其他参数
        /* 声明（创建）队列  queueDeclare( String queue, boolean durable, boolean exclusive, boolean autoDelete,  Map<String, Object> arguments)
         * queue - 队列名
         * durable - 是否是持久化队列， 队列的声明默认是存放到内存中的，如果rabbitmq重启会丢失
         * exclusie - 是否排外的，仅限于当前队列使用
         * autoDelete - 是否自动删除队列，当最后一个消费者断开连接之后队列是否自动被删除，可以通过界面 查看某个队列的消费者数量，当consumers = 0时队列就会自动删除
         * arguments - 队列携带的参数 比如 ttl-生命周期，x-dead-letter 死信队列等等
         */
        //channel.queueDeclare(queueName, true, false, false, Maps.newHashMap());

        for (int i = 0; i < 5; i++){
            String msg = "Hello World RabbitMQ" + i;

            Map<String, Object> headers = Maps.newHashMap();
            headers.put("flag", i);
            // deliveryMode 1:不持久化 2:持久化
            AMQP.BasicProperties props = new AMQP.BasicProperties().builder()
                    .deliveryMode(2)
                    .contentEncoding("UTF-8")
                    // TTL
                    .expiration("6000")
                    .headers(headers)
                    .build();

            // 参数 exchange
            // 不指定exchange,默认走AMQP default
            // 非持久化 服务器重启就没有了
            channel.basicPublish(exchangeName, routingKey, true, props, msg.getBytes());
        }
    }
}

package com.sr.rabbitmq.api.helloworld;

import com.google.common.collect.Maps;
import com.rabbitmq.client.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author shirui
 * @date 2021/2/1
 */
@Slf4j
public class Receiver {

    public static void main(String[] args) throws IOException, TimeoutException {
        exchangeQueueTTL();
    }

    /**
     * 不用exchange
     *
     * @throws IOException
     */
    public static void queue() throws IOException, TimeoutException {
        // 1 创建ConnectionFactory
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("172.16.57.153");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        // 自动恢复链接
        connectionFactory.setAutomaticRecoveryEnabled(true);
        // 设置间隔时间 如果恢复失败，RabbitMQ会固定时间间隔以后进行重试，默认为5秒钟
        connectionFactory.setNetworkRecoveryInterval(3000);

        // 2 创建connection
        Connection connection = connectionFactory.newConnection();

        // 3 创建channel
        Channel channel = connection.createChannel();

        // 4 声明
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

        Consumer consumer = new DefaultConsumer(channel){
            //消息的回调监听
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body,"UTF-8");
                log.info("queue,Accept:{},message:{}",envelope.getRoutingKey(),message);
//                System.out.println(consumerTag);
//                System.out.println(envelope.toString());
//                System.out.println(properties.toString());
//                System.out.println("消息内容:" + new String(body));
            }
        };

        // 消费者订阅消息 监听如上声明的队列 (队列名, 是否自动应答(与消息可靠有关 后续会介绍), 消费者标签, 消费者)
        // 消息确认机制
        // autoAck true:表示自动确认，只要消息从队列中获取，无论消费者获取到消息后是否成功消费，都会认为消息已经成功消费
        // autoAck false:表示手动确认，消费者获取消息后，服务器会将该消息标记为不可用状态，等待消费者的反馈，如果消费者一直没有反馈，那么该消息将一直处于不可用状态
        // 并且服务器会认为该消费者已经挂掉，不会再给其发送消息，直到该消费者反馈
        channel.basicConsume(queueName, true, "Receiver消费者", consumer);
    }

    /**
     * 使用exchange
     *
     * @throws IOException
     * @throws TimeoutException
     */
    public static void exchangeQueue() throws IOException, TimeoutException {
        // 1 创建ConnectionFactory
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("172.16.57.153");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        // 自动恢复链接
        connectionFactory.setAutomaticRecoveryEnabled(true);
        // 设置间隔时间 如果恢复失败，RabbitMQ会固定时间间隔以后进行重试，默认为5秒钟
        connectionFactory.setNetworkRecoveryInterval(3000);

        // 2 创建connection
        Connection connection = connectionFactory.newConnection();

        // 3 创建channel
        Channel channel = connection.createChannel();

        // 4 声明exchange
        channel.exchangeDeclare("exchange-test", BuiltinExchangeType.TOPIC, true, false, false, Maps.newHashMap());

        // 将队列Binding到交换机上 (队列名, 交换机名, Routing key, 绑定属性);
        channel.queueBind("queue-test", "exchange-test", "test.*", Maps.newHashMap());

        Consumer consumer = new DefaultConsumer(channel){
            //消息的回调监听
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body,"UTF-8");
                log.info("exchange-Accept:{},message:{}", envelope.getRoutingKey(), message);
//                System.out.println(consumerTag);
//                System.out.println(envelope.toString());
//                System.out.println(properties.toString());
//                System.out.println("消息内容:" + new String(body));
            }
        };

        // 消费者订阅消息 监听如上声明的队列 (队列名, 是否自动应答(与消息可靠有关 后续会介绍), 消费者标签, 消费者)
        // 消息确认机制
        // autoAck true:表示自动确认，只要消息从队列中获取，无论消费者获取到消息后是否成功消费，都会认为消息已经成功消费
        // autoAck false:表示手动确认，消费者获取消息后，服务器会将该消息标记为不可用状态，等待消费者的反馈，如果消费者一直没有反馈，那么该消息将一直处于不可用状态
        // 并且服务器会认为该消费者已经挂掉，不会再给其发送消息，直到该消费者反馈
        channel.basicConsume("queue-test", true, "Exchange-Receiver消费者", consumer);
    }

    /**
     * 使用exchange Ack Nack
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
        // 自动恢复链接
        connectionFactory.setAutomaticRecoveryEnabled(true);
        // 设置间隔时间 如果恢复失败，RabbitMQ会固定时间间隔以后进行重试，默认为5秒钟
        connectionFactory.setNetworkRecoveryInterval(3000);

        // 2 创建connection
        Connection connection = connectionFactory.newConnection();

        // 3 创建channel
        Channel channel = connection.createChannel();

        // 4 声明exchange
        channel.exchangeDeclare("exchange-test", BuiltinExchangeType.TOPIC, true, false, false, Maps.newHashMap());

        // 将队列Binding到交换机上 (队列名, 交换机名, Routing key, 绑定属性);
        channel.queueBind("queue-test", "exchange-test", "test.*", Maps.newHashMap());

        Consumer consumer = new DefaultConsumer(channel){
            //消息的回调监听
            @SneakyThrows
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body,"UTF-8");
                log.info("exchange-Accept:{},message:{}", envelope.getRoutingKey(), message);
                System.out.println((Integer)properties.getHeaders().get("flag"));
                if ((Integer)properties.getHeaders().get("flag") == 0){
                    channel.basicNack(envelope.getDeliveryTag(), false, false);
                }else{
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
//                System.out.println(consumerTag);
//                System.out.println(envelope.toString());
//                System.out.println(properties.toString());
//                System.out.println("消息内容:" + new String(body));
            }
        };

        // 消息大小 不要一次性推送大于N条的数据 是否应用整个通道
        channel.basicQos(0, 1, false);
        // 消费者订阅消息 监听如上声明的队列 (队列名, 是否自动应答(与消息可靠有关 后续会介绍), 消费者标签, 消费者)
        // 消息确认机制
        // autoAck true:表示自动确认，只要消息从队列中获取，无论消费者获取到消息后是否成功消费，都会认为消息已经成功消费
        // autoAck false:表示手动确认，消费者获取消息后，服务器会将该消息标记为不可用状态，等待消费者的反馈，如果消费者一直没有反馈，那么该消息将一直处于不可用状态
        // 并且服务器会认为该消费者已经挂掉，不会再给其发送消息，直到该消费者反馈
        channel.basicConsume("queue-test", false, "Exchange-Receiver消费者", consumer);
    }

    /**
     * 使用exchange TTL
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
        // 自动恢复链接
        connectionFactory.setAutomaticRecoveryEnabled(true);
        // 设置间隔时间 如果恢复失败，RabbitMQ会固定时间间隔以后进行重试，默认为5秒钟
        connectionFactory.setNetworkRecoveryInterval(3000);

        // 2 创建connection
        Connection connection = connectionFactory.newConnection();

        // 3 创建channel
        Channel channel = connection.createChannel();

        // 4 声明exchange
        String queueName = "test_dlx_queue";
        String exchangeName = "test_dlx_exchange";
        String routingKey = "group.*";
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC, true, false, false, Maps.newHashMap());

        // 设置死信队列
        Map<String, Object> arguments = Maps.newHashMap();
        arguments.put("x-dead-letter-exchange", "dlx.exchange");
        channel.queueDeclare(queueName, true, false, false, arguments);
        // 将队列Binding到交换机上 (队列名, 交换机名, Routing key, 绑定属性);
        channel.queueBind(queueName, exchangeName, routingKey);

        // dlx declare
        channel.exchangeDeclare("dlx.exchange", BuiltinExchangeType.TOPIC, true, false, false, null);
        channel.queueDeclare("dlx.queue", true, false, false, null);
        channel.queueBind("dlx.queue", "dlx.exchange", "#");

        Consumer consumer = new DefaultConsumer(channel){
            //消息的回调监听
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body,"UTF-8");
                log.info("queue,Accept:{},message:{}",envelope.getRoutingKey(),message);
//                System.out.println(consumerTag);
//                System.out.println(envelope.toString());
//                System.out.println(properties.toString());
//                System.out.println("消息内容:" + new String(body));
            }
        };

        // 消费者订阅消息 监听如上声明的队列 (队列名, 是否自动应答(与消息可靠有关 后续会介绍), 消费者标签, 消费者)
        // 消息确认机制
        // autoAck true:表示自动确认，只要消息从队列中获取，无论消费者获取到消息后是否成功消费，都会认为消息已经成功消费
        // autoAck false:表示手动确认，消费者获取消息后，服务器会将该消息标记为不可用状态，等待消费者的反馈，如果消费者一直没有反馈，那么该消息将一直处于不可用状态
        // 并且服务器会认为该消费者已经挂掉，不会再给其发送消息，直到该消费者反馈
        channel.basicConsume(queueName, true, "Exchange-Receiver消费者", consumer);
    }
}

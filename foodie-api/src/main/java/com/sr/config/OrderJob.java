package com.sr.config;

import com.sr.service.IOrderService;
import com.sr.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author shirui
 * @date 2020/2/17
 */
@Component
@Slf4j
public class OrderJob {

    private IOrderService iOrderService;

    @Autowired
    public void setiOrderService(IOrderService iOrderService) {
        this.iOrderService = iOrderService;
    }

    /**
     * 使用定时任务关闭超期未支付的订单，会存在弊端
     * 1. 会有时间差，程序不严谨
     *      例：10:39下单，11:00检查不足一小时，12:00检查，超时1小时21分钟
     * 2. 不支持集群
     *      单机使用没问题，使用集群后，就会有多个定时任务
     *      解决方案：只是用一台计算机节点，单独用来运行所有的定时任务
     * 3. 会对数据库全表搜索，及其影响性能：select * from order where orderStatus = 10;
     *    定时任务，仅仅只适用于小型轻量级项目、传统项目
     *
     * 集群方案
     *      使用消息队列：MQ-> RabbitMQ, RocketMQ, kafka, ZeroMQ ...
     *      延时任务（队列）
     *      例：10:39下单，未付款（10）状态，11:39分检查，如果当前状态还是10，则直接关闭订单即可
     */
    @Scheduled(cron = "0/3 * * * * ?")
    public void autoCloseOrder() {
        iOrderService.closeOrder();
        log.info("OrderJob 执行定时关闭订单任务，当前时间为：{}",
                 DateUtil.getCurrentDateString(DateUtil.STANDARD_FORMATTER));
    }
}

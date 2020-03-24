package com.sr.service;

import com.sr.pojo.OrderStatus;
import com.sr.pojo.bo.SubmitOrderBO;
import com.sr.pojo.vo.OrderVO;

/**
 * @author shirui
 * @date 2020/2/13
 */
public interface IOrderService {

    /**
     * 用户创建订单相关信息
     * @param submitOrderBo
     * @return
     */
    OrderVO createOrder(SubmitOrderBO submitOrderBo);

    /**
     * 修改订单状态
     * @param orderId
     * @param orderStatus
     */
    void updateOrderStatus(String orderId, Integer orderStatus);

    /**
     * 查询订单状态
     * @param orderId
     * @return
     */
    OrderStatus queryOrderStatusInfo(String orderId);

    /**
     * 关闭超时未支付订单
     */
    void closeOrder();
}

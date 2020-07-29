package com.sr.service;

import com.sr.pojo.OrderStatus;
import com.sr.pojo.bo.ShopCartBO;
import com.sr.pojo.bo.SubmitOrderBO;
import com.sr.pojo.vo.OrderVO;

import java.util.List;

/**
 * @author shirui
 * @date 2020/2/13
 */
public interface IOrderService {

    /**
     * 用户订单创建相关信息
     * @param shopcartBOList
     * @param submitOrderBo
     * @return
     */
    OrderVO createOrder(List<ShopCartBO> shopcartBOList, SubmitOrderBO submitOrderBo);

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

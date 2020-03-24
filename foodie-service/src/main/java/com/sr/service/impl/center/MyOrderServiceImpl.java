package com.sr.service.impl.center;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.sr.custom.OrderCustomMapper;
import com.sr.enums.OrderStatusEnum;
import com.sr.enums.YesOrNoEnum;
import com.sr.mapper.OrderStatusMapper;
import com.sr.mapper.OrdersMapper;
import com.sr.pojo.OrderStatus;
import com.sr.pojo.Orders;
import com.sr.pojo.vo.OrderStatusCountsVO;
import com.sr.pojo.vo.center.MyOrdersVO;
import com.sr.service.center.IMyOrdersService;
import com.sr.utils.PageGridResult;
import com.sr.utils.PageGridResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author shirui
 * @date 2020/2/24
 */
@Service("iMyOrdersService")
public class MyOrderServiceImpl implements IMyOrdersService {

    private OrderCustomMapper orderCustomMapper;

    private OrdersMapper ordersMapper;

    private OrderStatusMapper orderStatusMapper;

    @Autowired
    public void setOrderCustomMapper(OrderCustomMapper orderCustomMapper) {
        this.orderCustomMapper = orderCustomMapper;
    }

    @Autowired
    public void setOrdersMapper(OrdersMapper ordersMapper) {
        this.ordersMapper = ordersMapper;
    }

    @Autowired
    public void setOrderStatusMapper(OrderStatusMapper orderStatusMapper) {
        this.orderStatusMapper = orderStatusMapper;
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public PageGridResult queryMyOrders(String userId, Integer orderStatus, Integer page, Integer pageSize) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("userId", userId);

        if (orderStatus != null){
            map.put("orderStatus", orderStatus);
        }

        PageHelper.startPage(page, pageSize);
        List<MyOrdersVO> myOrdersVOList = orderCustomMapper.queryMyOrders(map);

        return PageGridResultUtil.setterPageGrid(myOrdersVOList, page);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public void updateDeliverOrderStatus(String orderId) {
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderStatus(OrderStatusEnum.WAIT_RECEIVE.getType());
        orderStatus.setDeliverTime(new Date());

        Example example = new Example(OrderStatus.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId", orderId);
        criteria.andEqualTo("orderStatus", OrderStatusEnum.WAIT_DELIVER.getValue());

        orderStatusMapper.updateByExampleSelective(orderStatus, example);
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public Orders queryMyOrder(String userId, String orderId) {
        Orders orders = new Orders();
        orders.setUserId(userId);
        orders.setId(orderId);
        orders.setIsDelete(YesOrNoEnum.NO.getType());

        return ordersMapper.selectOne(orders);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public boolean updateReceiveOrderStatus(String orderId) {
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderStatus(OrderStatusEnum.SUCCESS.getType());
        orderStatus.setSuccessTime(new Date());

        Example example = new Example(OrderStatus.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId", orderId);
        criteria.andEqualTo("orderStatus", OrderStatusEnum.WAIT_RECEIVE.getValue());

        int result = orderStatusMapper.updateByExampleSelective(orderStatus, example);

        return 1 == result ? true : false;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public boolean deleteOrder(String userId, String orderId) {
        Orders updateOrder = new Orders();
        updateOrder.setIsDelete(YesOrNoEnum.YES.getType());
        updateOrder.setUpdatedTime(new Date());

        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", orderId);
        criteria.andEqualTo("userId", userId);

        int result = ordersMapper.updateByExampleSelective(updateOrder, example);

        return 1 == result ? true : false;
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public OrderStatusCountsVO getOrderStatusCounts(String userId) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("userId", userId);

        map.put("orderStatus", OrderStatusEnum.WAIT_PAY.getType());
        int waitPayCount = orderCustomMapper.getMyOrderStatusCounts(map);

        map.put("orderStatus", OrderStatusEnum.WAIT_DELIVER.getType());
        int waitDeliverCounts = orderCustomMapper.getMyOrderStatusCounts(map);

        map.put("orderStatus", OrderStatusEnum.WAIT_RECEIVE.getType());
        int waitReceiveCounts = orderCustomMapper.getMyOrderStatusCounts(map);

        map.put("orderStatus", OrderStatusEnum.SUCCESS.getType());
        map.put("isComment", YesOrNoEnum.NO.getType());
        int waitCommentCounts = orderCustomMapper.getMyOrderStatusCounts(map);

        OrderStatusCountsVO orderStatusCountsVO = new OrderStatusCountsVO(waitPayCount,
                                                                            waitDeliverCounts,
                                                                            waitReceiveCounts,
                                                                            waitCommentCounts);

        return orderStatusCountsVO;
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public PageGridResult getOrdersTrend(String userId, Integer page, Integer pageSize) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("userId", userId);

        PageHelper.startPage(page, pageSize);
        List<OrderStatus> orderStatusList = orderCustomMapper.getMyOrderTrend(map);

        return PageGridResultUtil.setterPageGrid(orderStatusList, page);
    }
}

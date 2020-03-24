package com.sr.service.impl.center;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.sr.custom.ItemsCommentsCustomMapper;
import com.sr.enums.YesOrNoEnum;
import com.sr.mapper.OrderItemsMapper;
import com.sr.mapper.OrderStatusMapper;
import com.sr.mapper.OrdersMapper;
import com.sr.pojo.OrderItems;
import com.sr.pojo.OrderStatus;
import com.sr.pojo.Orders;
import com.sr.pojo.bo.center.OrderItemsCommentBO;
import com.sr.pojo.vo.MyCommentVO;
import com.sr.service.center.IMyCommentsService;
import com.sr.utils.PageGridResult;
import com.sr.utils.PageGridResultUtil;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author shirui
 * @date 2020/2/24
 */
@Service("iMyCommentsService")
public class MyCommentServiceImpl implements IMyCommentsService {

    private OrderItemsMapper orderItemsMapper;

    private OrdersMapper ordersMapper;

    private OrderStatusMapper orderStatusMapper;

    private ItemsCommentsCustomMapper itemsCommentsCustomMapper;

    private Sid sid;

    @Autowired
    public void setOrderItemsMapper(OrderItemsMapper orderItemsMapper) {
        this.orderItemsMapper = orderItemsMapper;
    }

    @Autowired
    public void setOrdersMapper(OrdersMapper ordersMapper) {
        this.ordersMapper = ordersMapper;
    }

    @Autowired
    public void setOrderStatusMapper(OrderStatusMapper orderStatusMapper) {
        this.orderStatusMapper = orderStatusMapper;
    }

    @Autowired
    public void setItemsCommentsCustomMapper(ItemsCommentsCustomMapper itemsCommentsCustomMapper) {
        this.itemsCommentsCustomMapper = itemsCommentsCustomMapper;
    }

    @Autowired
    public void setSid(Sid sid) {
        this.sid = sid;
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public List<OrderItems> queryPendingComment(String orderId) {
        OrderItems orderItems = new OrderItems();
        orderItems.setOrderId(orderId);
        return orderItemsMapper.select(orderItems);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public void saveComments(String orderId, String userId, List<OrderItemsCommentBO> commentBOList) {
        // 1. 保存评价 items_comments
        for (OrderItemsCommentBO orderItemsCommentBO : commentBOList) {
            orderItemsCommentBO.setCommentId(sid.nextShort());
        }
        Map<String, Object> maps = Maps.newHashMap();
        maps.put("userId", userId);
        maps.put("commentList", commentBOList);
        itemsCommentsCustomMapper.saveComments(maps);

        // 2. 修改订单表改已评价 orders
        Orders order = new Orders();
        order.setId(orderId);
        order.setIsComment(YesOrNoEnum.YES.getType());
        ordersMapper.updateByPrimaryKeySelective(order);

        // 3. 修改订单状态表的留言时间 order_status
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setCommentTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public PageGridResult queryMyComments(String userId, Integer page, Integer pageSize) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("userId", userId);

        PageHelper.startPage(page, pageSize);
        List<MyCommentVO> myCommentVoList = itemsCommentsCustomMapper.queryMyComments(map);

        return PageGridResultUtil.setterPageGrid(myCommentVoList, page);
    }
}

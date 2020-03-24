package com.sr.service.impl;

import com.sr.enums.OrderStatusEnum;
import com.sr.enums.YesOrNoEnum;
import com.sr.mapper.OrderItemsMapper;
import com.sr.mapper.OrderStatusMapper;
import com.sr.mapper.OrdersMapper;
import com.sr.pojo.*;
import com.sr.pojo.bo.SubmitOrderBO;
import com.sr.pojo.vo.MerchantOrdersVO;
import com.sr.pojo.vo.OrderVO;
import com.sr.service.IAddressService;
import com.sr.service.IItemService;
import com.sr.service.IOrderService;
import com.sr.utils.DateUtil;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author shirui
 * @date 2020/2/13
 */
@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {

    private OrdersMapper ordersMapper;

    private OrderItemsMapper orderItemsMapper;

    private OrderStatusMapper orderStatusMapper;

    private IAddressService iAddressService;

    private IItemService iItemService;

    private Sid sid;

    @Autowired
    public void setOrdersMapper(OrdersMapper ordersMapper) {
        this.ordersMapper = ordersMapper;
    }

    @Autowired
    public void setSid(Sid sid) {
        this.sid = sid;
    }

    @Autowired
    public void setiAddressService(IAddressService iAddressService) {
        this.iAddressService = iAddressService;
    }

    @Autowired
    public void setiItemService(IItemService iItemService) {
        this.iItemService = iItemService;
    }

    @Autowired
    public void setOrderItemsMapper(OrderItemsMapper orderItemsMapper) {
        this.orderItemsMapper = orderItemsMapper;
    }

    @Autowired
    public void setOrderStatusMapper(OrderStatusMapper orderStatusMapper) {
        this.orderStatusMapper = orderStatusMapper;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public OrderVO createOrder(SubmitOrderBO submitOrderBo) {
        String userId = submitOrderBo.getUserId();
        String addressId = submitOrderBo.getAddressId();
        String itemSpecIds = submitOrderBo.getItemSpecIds();
        Integer payMethod = submitOrderBo.getPayMethod();
        String leftMsg = submitOrderBo.getLeftMsg();
        // 包邮费用设置为0
        Integer postAmount = 0;

        String orderId = sid.nextShort();

        UserAddress userAddress = iAddressService.queryUserAddress(userId, addressId);

        // 1. 新订单数据保存
        Orders orders = new Orders();
        orders.setId(orderId);
        orders.setUserId(userId);
        orders.setReceiverName(userAddress.getReceiver());
        orders.setReceiverMobile(userAddress.getMobile());
        orders.setReceiverAddress(userAddress.getProvince() + " " +
                                  userAddress.getCity() + " " +
                                  userAddress.getDistrict() + " " +
                                  userAddress.getDetail());
        orders.setPostAmount(postAmount);
        orders.setPayMethod(payMethod);
        orders.setLeftMsg(leftMsg);
        orders.setIsComment(YesOrNoEnum.NO.getType());
        orders.setIsDelete(YesOrNoEnum.NO.getType());
        orders.setCreatedTime(new Date());
        orders.setUpdatedTime(new Date());

        // 2. 循环根据itemSpecIds保存订单商品信息
        String[] itemSpecIdArr = itemSpecIds.split(",");
        // 商品原价累计
        Integer totalAmount = 0;
        // 优惠后的实际支付价格累计
        Integer realPayAmount = 0;

        for (String itemSpecId : itemSpecIdArr) {

            // TODO 整合redis后，商品购买的数量重新从redis的购物车中获取
            int butCounts = 1;

            // 2.1 根据规格id，查询规格的具体信息，主要获取价格
            ItemsSpec itemsSpec = iItemService.queryItemSpecById(itemSpecId);
            totalAmount += itemsSpec.getPriceNormal() * butCounts;
            realPayAmount += itemsSpec.getPriceDiscount() * butCounts;

            // 2.2 根据商品Id，获得商品信息以及商品图片
            String itemId = itemsSpec.getItemId();
            Items items = iItemService.queryItemById(itemId);
            String imgUrl = iItemService.queryItemMainImgById(itemId);

            // 2.3 循环爆粗订单数据到数据库
            String subOrderId = sid.nextShort();
            OrderItems subOrderItem = new OrderItems();
            subOrderItem.setId(subOrderId);
            subOrderItem.setOrderId(orderId);
            subOrderItem.setItemId(itemId);
            subOrderItem.setItemName(items.getItemName());
            subOrderItem.setItemImg(imgUrl);
            subOrderItem.setBuyCounts(butCounts);
            subOrderItem.setItemSpecId(itemSpecId);
            subOrderItem.setItemSpecName(itemsSpec.getName());
            subOrderItem.setPrice(itemsSpec.getPriceDiscount());
            orderItemsMapper.insert(subOrderItem);

            // 2.4 在用户提交订单以后，规格表中需要扣除库存
            iItemService.decreaseItemSpecStock(itemSpecId, butCounts);
        }

        orders.setTotalAmount(totalAmount);
        orders.setRealPayAmount(realPayAmount);
        ordersMapper.insert(orders);

        // 3. 保存订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.getType());
        orderStatus.setCreatedTime(new Date());
        orderStatusMapper.insert(orderStatus);

        // 4. 构建商户订单，用于传给支付中心
        MerchantOrdersVO merchantOrdersVo = new MerchantOrdersVO();
        merchantOrdersVo.setMerchantOrderId(orderId);
        merchantOrdersVo.setMerchantUserId(userId);
        merchantOrdersVo.setAmount(realPayAmount + postAmount);
        merchantOrdersVo.setPayMethod(payMethod);

        // 5. 构建自定义订单vo
        OrderVO orderVo = new OrderVO();
        orderVo.setOrderId(orderId);
        orderVo.setMerchantOrdersVo(merchantOrdersVo);

        return orderVo;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public void updateOrderStatus(String orderId, Integer orderStatus) {
        OrderStatus paidStatus = new OrderStatus();
        paidStatus.setOrderId(orderId);
        paidStatus.setOrderStatus(orderStatus);
        paidStatus.setPayTime(new Date());

        orderStatusMapper.updateByPrimaryKeySelective(paidStatus);
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public OrderStatus queryOrderStatusInfo(String orderId) {
        return orderStatusMapper.selectByPrimaryKey(orderId);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public void closeOrder() {
        // 查询所有未付款订单，判断时间是否超时（1天），超时则关闭交易
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.getType());
        List<OrderStatus> orderStatusList = orderStatusMapper.select(orderStatus);
        for (OrderStatus os : orderStatusList) {
            // 获得订单创建时间
            Date createdTime = new Date();
            // 和当前时间进行比对
            int days = DateUtil.daysBetween(createdTime, new Date());
            if (days >= 1){
                doCloseOrder(os.getOrderId());
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, RuntimeException.class})
    void doCloseOrder(String orderId){
        OrderStatus close = new OrderStatus();
        close.setOrderId(orderId);
        close.setOrderStatus(OrderStatusEnum.CLOSE.getType());
        close.setCloseTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(close);
    }
}

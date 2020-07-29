package com.sr.center;

import com.sr.pojo.Orders;
import com.sr.pojo.vo.OrderStatusCountsVO;
import com.sr.service.center.IMyOrdersService;
import com.sr.utils.PageGridResult;
import com.sr.utils.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shirui
 * @date 2020/3/1
 */
@Api(value = "用户中心我的订单", tags = {"用户中心我的订单相关接口"})
@RestController
@RequestMapping("/myorders")
public class MyOrdersController {

    private IMyOrdersService iMyOrdersService;

    @Autowired
    public void setiMyOrdersService(IMyOrdersService iMyOrdersService) {
        this.iMyOrdersService = iMyOrdersService;
    }

    @ApiOperation(value = "获取订单状态概述情况", notes = "获取订单概述情况", httpMethod = "POST")
    @PostMapping("statusCounts")
    public ServerResponse statusCounts(
            @ApiParam(name = "userId", value = "userId", required = true)
            @RequestParam String userId){
        if (StringUtils.isBlank(userId)){
            return ServerResponse.createByErrorMessage(null);
        }

        OrderStatusCountsVO result = iMyOrdersService.getOrderStatusCounts(userId);

        return ServerResponse.createBySuccess(result);
    }

    @ApiOperation(value = "查询订单列表", notes = "查询订单列表", httpMethod = "POST")
    @PostMapping("/query")
    public ServerResponse query(
            @ApiParam(name = "userId", value = "用户Id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderStatus", value = "订单状态", required = false)
            @RequestParam Integer orderStatus,
            @ApiParam(name = "page", value = "查询下一页", required = false)
            @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(name = "pageSize", value = "每页查询条数", required = false)
            @RequestParam(defaultValue = "10") Integer pageSize){
        if (StringUtils.isBlank(userId)){
            return ServerResponse.createByErrorMessage(null);
        }

        PageGridResult result = iMyOrdersService.queryMyOrders(userId, orderStatus, page, pageSize);

        return ServerResponse.createBySuccess(result);
    }

    /**
     * 商户发货没有后端，这个接口仅仅用于模拟
     * @param orderId
     * @return
     */
    @ApiOperation(value = "商家发货", notes = "商家发货", httpMethod = "POST")
    @PostMapping("/deliver")
    public ServerResponse deliver(
            @ApiParam(name = "orderId", value = "订单Id", required = true)
            @RequestParam String orderId){
        if (StringUtils.isBlank(orderId)){
            return ServerResponse.createByErrorMessage("订单Id不能为空");
        }

        iMyOrdersService.updateDeliverOrderStatus(orderId);

        return ServerResponse.createBySuccess();
    }

    @ApiOperation(value = "用户确认收货", notes = "用户确认收货", httpMethod = "POST")
    @PostMapping("/confirmReceive")
    public ServerResponse confirmReceive(
            @ApiParam(name = "orderId", value = "订单Id", required = true)
            @RequestParam String orderId,
            @ApiParam(name = "userId", value = "用户Id", required = true)
            @RequestParam String userId){
        ServerResponse serverResponse = checkUserOrder(userId, orderId);
        if (serverResponse.getStatus() != HttpStatus.OK.value()){
            return serverResponse;
        }

        boolean flag = iMyOrdersService.updateReceiveOrderStatus(orderId);
        if (!flag){
            return ServerResponse.createByErrorMessage("订单确认收货失败!");
        }

        return ServerResponse.createBySuccess();
    }

    @ApiOperation(value = "用户订单删除", notes = "用户删除订单", httpMethod = "POST")
    @PostMapping("/delete")
    public ServerResponse delete(
            @ApiParam(name = "orderId", value = "订单Id", required = true)
            @RequestParam String orderId,
            @ApiParam(name = "userId", value = "用户Id", required = false)
            @RequestParam String userId){
        ServerResponse serverResponse = checkUserOrder(userId, orderId);
        if (serverResponse.getStatus() != HttpStatus.OK.value()){
            return serverResponse;
        }

        boolean flag = iMyOrdersService.deleteOrder(userId, orderId);
        if (!flag){
            return ServerResponse.createByErrorMessage("订单删除失败");
        }

        return ServerResponse.createBySuccess();
    }

    @ApiOperation(value = "查询订单动向", notes = "查询订单动向", httpMethod = "POST")
    @PostMapping("/trend")
    public ServerResponse trend(
            @ApiParam(name = "userId", value = "用户Id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "page", value = "查询下一页", required = false)
            @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(name = "pageSize", value = "每页查询条数", required = false)
            @RequestParam(defaultValue = "10") Integer pageSize){
        if (StringUtils.isBlank(userId)){
            return ServerResponse.createByErrorMessage(null);
        }

        PageGridResult result = iMyOrdersService.getOrdersTrend(userId, page, pageSize);

        return ServerResponse.createBySuccess(result);
    }

    private ServerResponse checkUserOrder(String userId, String orderId){
        Orders order = iMyOrdersService.queryMyOrder(userId, orderId);
        if (order  == null){
            return ServerResponse.createByErrorMessage("订单不存在!");
        }
        return ServerResponse.createBySuccess(order);
    }
}

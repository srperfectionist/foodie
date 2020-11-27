package com.sr.controller.center;

import com.sr.enums.YesOrNoEnum;
import com.sr.pojo.OrderItems;
import com.sr.pojo.Orders;
import com.sr.pojo.bo.center.OrderItemsCommentBO;
import com.sr.service.center.IMyCommentsService;
import com.sr.service.center.IMyOrdersService;
import com.sr.utils.PageGridResult;
import com.sr.utils.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author shirui
 * @date 2020/2/29
 */
@Api(value = "用户中心评价模块", tags = {"用户中心评价模块相关接口"})
@RestController
@RequestMapping("/mycomments")
public class MyCommentsController {

    private IMyCommentsService iMyCommentsService;

    private IMyOrdersService iMyOrdersService;

    @Autowired
    public void setiMyCommentsService(IMyCommentsService iMyCommentsService) {
        this.iMyCommentsService = iMyCommentsService;
    }

    @Autowired
    public void setiMyOrdersService(IMyOrdersService iMyOrdersService) {
        this.iMyOrdersService = iMyOrdersService;
    }

    @ApiOperation(value = "查询订单列表", notes = "查询订单列表", httpMethod = "POST")
    @PostMapping("/pending")
    public ServerResponse pending(
            @ApiParam(name = "userId", value = "用户Id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderId", value = "订单Id", required = false)
            @RequestParam String orderId){

        // 判断用户和订单是否关联
        ServerResponse serverResponse = checkUserOrder(userId, orderId);
        if (HttpStatus.OK.value() != serverResponse.getStatus()){
            return serverResponse;
        }

        // 判断该笔订单是否已经评价过，评价过了就不再继续
        Orders order = (Orders) serverResponse.getData();
        if (order.getIsComment().equals(YesOrNoEnum.YES.getType())){
            return ServerResponse.createByErrorMessage("该笔订单已经评价!");
        }

        List<OrderItems> orderItemsList = iMyCommentsService.queryPendingComment(orderId);

        return ServerResponse.createBySuccess(orderItemsList);
    }

    @ApiOperation(value = "保存评论列表", notes = "保存评论列表", httpMethod = "POST")
    @PostMapping("/saveList")
    public ServerResponse saveList(
            @ApiParam(name = "userId", value = "用户Id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderId", value = "订单Id", required = false)
            @RequestParam String orderId,
            @RequestBody List<OrderItemsCommentBO> commentBOList){

        // 判断用户和订单是否关联
        ServerResponse serverResponse = checkUserOrder(userId, orderId);
        if (HttpStatus.OK.value() != serverResponse.getStatus()){
            return serverResponse;
        }

        // 判断评论内容List不能为空
        if (CollectionUtils.isEmpty(commentBOList)){
            return ServerResponse.createByErrorMessage("评论内容不能为空!");
        }

        iMyCommentsService.saveComments(orderId, userId, commentBOList);

        return ServerResponse.createBySuccess();
    }

    @ApiOperation(value = "查询我的评价", notes = "查询我的评价", httpMethod = "POST")
    @PostMapping("/query")
    public ServerResponse query(
            @ApiParam(name = "userId", value = "用户Id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "page", value = "查询下一页", required = false)
            @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(name = "pageSize", value = "每一天显示的条数", required = false)
            @RequestParam(defaultValue = "10") Integer pageSize){
        if (StringUtils.isBlank(userId)){
            return ServerResponse.createByErrorMessage(null);
        }

        PageGridResult result = iMyCommentsService.queryMyComments(userId, page, pageSize);

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

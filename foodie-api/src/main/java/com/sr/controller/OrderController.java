package com.sr.controller;

import com.sr.enums.OrderStatusEnum;
import com.sr.enums.PayMethodEnum;
import com.sr.pojo.OrderStatus;
import com.sr.pojo.bo.SubmitOrderBO;
import com.sr.pojo.vo.MerchantOrdersVO;
import com.sr.pojo.vo.OrderVO;
import com.sr.resource.Natapp;
import com.sr.resource.Payment;
import com.sr.service.IOrderService;
import com.sr.utils.IMOOCJSONResult;
import com.sr.utils.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author shirui
 * @date 2020/2/17
 */
@Api(value = "订单相关", tags = {"订单相关的api接口"})
@RequestMapping("/orders")
@RestController
@Slf4j
public class OrderController {

    private IOrderService iOrderService;

    private RestTemplate restTemplate;

    private Natapp natapp;

    private Payment payment;

    @Autowired
    public void setiOrderService(IOrderService iOrderService) {
        this.iOrderService = iOrderService;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    public void setNatapp(Natapp natapp) {
        this.natapp = natapp;
    }

    @Autowired
    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    @ApiOperation(value = "用户下单", notes = "用户下单", httpMethod = "POST")
    @PostMapping("/create")
    public ServerResponse create(
            @RequestBody SubmitOrderBO submitOrderBO,
            HttpServletRequest request,
            HttpServletResponse response) {

        if (!submitOrderBO.getPayMethod().equals(PayMethodEnum.WEIXIN.getType())
                && !submitOrderBO.getPayMethod().equals(PayMethodEnum.ALIPAY.getType())) {
            return ServerResponse.createByErrorMessage("支付方式不支持！");
        }

        // 1. 创建订单
        OrderVO orderVO = iOrderService.createOrder(submitOrderBO);
        String orderId = orderVO.getOrderId();

        // 2. 创建订单以后，移除购物车中已结算（已提交）的商品

        // TODO 整合redis之后，完善购物车中的已结算商品清除，并且同步到前端的cookie
        // CookieUtils.setCookie(request, response, FOODIE_SHOPCART, "", true);

        // 3. 向支付中心发送当前订单，用于保存支付中心的订单数据
        MerchantOrdersVO merchantOrdersVO = orderVO.getMerchantOrdersVo();
        merchantOrdersVO.setReturnUrl(natapp.getPayReturnUrl());

        // 为了方便测试购买，所以所有的支付金额都统一改为1分钱
        merchantOrdersVO.setAmount(1);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("imoocUserId", "imooc");
        headers.add("password", "imooc");

        HttpEntity<MerchantOrdersVO> entity =
                new HttpEntity<>(merchantOrdersVO, headers);

        ResponseEntity<IMOOCJSONResult> responseEntity = restTemplate.postForEntity(payment.getPaymentUrl(), entity,
                IMOOCJSONResult.class);
        IMOOCJSONResult paymentResult = responseEntity.getBody();
        if (paymentResult.getStatus() != 200) {
            log.error("发送错误：{}", paymentResult.getMsg());
            return ServerResponse.createByErrorMessage("支付中心订单创建失败，请联系管理员！");
        }

        return ServerResponse.createBySuccess(orderId);
    }

    @PostMapping("/notifyMerchantOrderPaid")
    public Integer notifyMerchantOrderPaid(String merchantOrderId) {
        iOrderService.updateOrderStatus(merchantOrderId, OrderStatusEnum.WAIT_DELIVER.getType());
        return HttpStatus.OK.value();
    }

    @PostMapping("/getPaidOrderInfo")
    public ServerResponse getPaidOrderInfo(String orderId) {
        OrderStatus orderStatus = iOrderService.queryOrderStatusInfo(orderId);
        return ServerResponse.createBySuccess(orderStatus);
    }
}

package com.sr.controller;

import com.sr.pojo.bo.ShopCartBO;
import com.sr.utils.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author SR
 * @date 2020/1/12
 */
@Api(value = "购物车接口controller", tags = {"购物车接口相关api"})
@RestController
@RequestMapping("/shopcart")
public class ShopCartController {

    @ApiOperation(value = "添加商品到购物车", notes = "添加商品到购物城", httpMethod = "GET")
    @GetMapping("/add")
    public ServerResponse add(@RequestParam("userId") String userId,
                              @RequestBody ShopCartBO shopCartBo,
                              HttpServletRequest request,
                              HttpServletResponse response){
        if (StringUtils.isBlank(userId)){
            return ServerResponse.createByErrorMessage("用户Id不能为空");
        }

        return ServerResponse.createBySuccess();
    }
}

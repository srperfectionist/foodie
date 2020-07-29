package com.sr;

import com.google.common.collect.Lists;
import com.sr.pojo.bo.ShopCartBO;
import com.sr.utils.JsonUtil;
import com.sr.utils.RedisOperator;
import com.sr.utils.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.model.PreferredConstructorDiscoverer;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author SR
 * @date 2020/1/12
 */
@Api(value = "购物车接口controller", tags = {"购物车接口相关api"})
@RestController
@RequestMapping("/shopcart")
public class ShopCartController {

    private RedisOperator redisOperator;

    @Autowired
    public void setRedisOperator(RedisOperator redisOperator) {
        this.redisOperator = redisOperator;
    }

    @ApiOperation(value = "添加商品到购物车", notes = "添加商品到购物城", httpMethod = "POST")
    @PostMapping("/add")
    public ServerResponse add(@RequestParam("userId") String userId,
                              @RequestBody ShopCartBO shopCartBo,
                              HttpServletRequest request,
                              HttpServletResponse response){
        if (StringUtils.isBlank(userId)){
            return ServerResponse.createByErrorMessage("用户Id不能为空");
        }

        // 前段用户在登录的情况，添加商品到购物车，会同时在后端同步购物车到redis缓存
        // 需要判断当前购物车中包含已经存在的商品，如果存在则累加购买数量

        String shopCartJson = redisOperator.get("shopcart:" + userId);
        List<ShopCartBO> shopCartBOList = Lists.newArrayList();
        if (StringUtils.isNotBlank(shopCartJson)){
            // redis中已经有购物车了
            shopCartBOList = JsonUtil.jsonToList(shopCartJson, ShopCartBO.class);
            // 判断购物车中是否存在已有商品，如果有的话counts累加
            boolean isHaving = false;
            for (ShopCartBO sc : shopCartBOList){
                String tmpSpecId = sc.getSpecId();
                if (StringUtils.equals(tmpSpecId, shopCartBo.getSpecId())){
                    sc.setBuyCounts(sc.getBuyCounts() + shopCartBo.getBuyCounts());
                    isHaving = true;
                }
            }
            if (!isHaving){
                shopCartBOList.add(shopCartBo);
            }
        } else{
            // redis中没有购物车，直接添加到购车中
            shopCartBOList.add(shopCartBo);
        }

        // 覆盖现有redis中的购物车
        redisOperator.set("shopcart:" + userId, JsonUtil.objToString(shopCartBOList));

        return ServerResponse.createBySuccess();
    }

    @ApiOperation(value = "从购物车中删除商品", notes = "从购物车中删除商品", httpMethod = "POST")
    @PostMapping("/del")
    public ServerResponse del(@RequestParam("userId") String userId,
                              @RequestParam("itemSpecId") String itemSpecId,
                              HttpServletRequest request,
                              HttpServletResponse response){
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(itemSpecId)){
            return ServerResponse.createByErrorMessage("参数不能为空");
        }

        // 用户在页面删除购物车中的商品数据，如果此时用户已经登录，则需要同步删除redis购物车中的商品
        String shopCartJson = redisOperator.get("shopcart:" + userId);
        List<ShopCartBO> shopCartBOList = Lists.newArrayList();
        if (StringUtils.isNotBlank(shopCartJson)){
            // redis中已经有购物车了
            shopCartBOList = JsonUtil.jsonToList(shopCartJson, ShopCartBO.class);
            // 判断购物车中是否存在已有商品，如果有的话counts累加
            for (ShopCartBO sc : shopCartBOList){
                String tmpSpecId = sc.getSpecId();
                if (StringUtils.equals(tmpSpecId, itemSpecId)){
                    shopCartBOList.remove(sc);
                    break;
                }
            }

            // 覆盖现有redis中的购物车
            redisOperator.set("shopcart:" + userId, JsonUtil.objToString(shopCartBOList));
        }

        return ServerResponse.createBySuccess();
    }
}

package com.sr;

import com.google.common.collect.Lists;
import com.sr.pojo.Users;
import com.sr.pojo.bo.ShopCartBO;
import com.sr.pojo.bo.UserBO;
import com.sr.pojo.vo.UserVO;
import com.sr.service.IUserService;
import com.sr.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

/**
 * @author SR
 * @date 2019/11/19
 */
@Api(value = "注册登录", tags = {"用于注册登录的相关接口"})
@RestController
@RequestMapping("/passport")
@Slf4j
public class PassportController {

    private IUserService iUserService;

    private RedisOperator redisOperator;

    @Autowired
    public void setIUserService(IUserService iUserService){
        this.iUserService = iUserService;
    }

    @Autowired
    public void setRedisOperator(RedisOperator redisOperator) {
        this.redisOperator = redisOperator;
    }

    @ApiOperation(value = "用户名是否存在", notes="用户名是否存在", httpMethod = "GET")
    @GetMapping("/usernameIsExist")
    public ServerResponse usernameIsExist(@ApiParam(name="username", value = "用户名称", required = true) @RequestParam String username){
        // 1. 判断用户名不能为空
        if(StringUtils.isBlank(username)){
            return ServerResponse.createByErrorMessage("用户名不能为空");
        }

        // 2. 查找注册的用户名是否存在
        boolean isExist = iUserService.queryUsernameIsExist(username);
        if(!isExist){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        return ServerResponse.createBySuccessMessage("OK");
    }

    @ApiOperation(value = "用户注册", notes = "用户注册", httpMethod = "POST")
    @PostMapping("/register")
    public ServerResponse register(@RequestBody UserBO userBo,
                                   HttpServletRequest request,
                                   HttpServletResponse response){
        String username = userBo.getUsername();
        String password = userBo.getPassword();
        String confirmPassword = userBo.getConfirmPassword();
        int passwordLength = 6;

        // 0. 判断用户名和密码不能为空
        if (StringUtils.isBlank(username) ||
            StringUtils.isBlank(password) ||
            StringUtils.isBlank(confirmPassword)){
            return ServerResponse.createByErrorMessage("用户名或密码不能为空");
        }

        // 1. 查询用户名是否存在
        boolean isExist = iUserService.queryUsernameIsExist(username);
        if (!isExist){
            return ServerResponse.createByErrorMessage("用户名已经存在");
        }

        // 2. 密码长度不能小于6位
        if (password.length() < passwordLength){
            return ServerResponse.createByErrorMessage("密码长度不能小于6位");
        }

        // 3. 判断两次密码是否一致
        if (!StringUtils.equals(password, confirmPassword)){
            return ServerResponse.createByErrorMessage("两次密码输入不一样");
        }

        // 4. 注册
        Users userResult = iUserService.creatUser(userBo);

        // 同步购物车数据
        syncShopCartData(userResult.getId(), request, response);

        return getServerResponse(request, response, userResult);
    }

    @ApiOperation(value = "用户登录", notes = "用户登录", httpMethod = "POST")
    @PostMapping("/login")
    public ServerResponse login(@RequestBody UserBO userBo,
                                HttpServletRequest request,
                                HttpServletResponse response){
        String username = userBo.getUsername();
        String password = userBo.getPassword();

        // 0. 判断用户名和密码不能为空
        if (StringUtils.isBlank(username) ||
                StringUtils.isBlank(password)){
            return ServerResponse.createByErrorMessage("用户名或密码不能为空");
        }

        // 1. 登录
        Users userResult = iUserService.queryUserForLogin(username, MD5Util.MD5EncodeUtf8(password));

        if (userResult == null){
            return ServerResponse.createByErrorMessage("用户名或密码不正确");
        }

        // 同步购车数据
        syncShopCartData(userResult.getId(), request, response);

        return getServerResponse(request, response, userResult);
    }

    @ApiOperation(value = "用户退出登录", notes = "用户退出登录", httpMethod = "POST")
    @PostMapping("/logout")
    public ServerResponse logout(@RequestParam String userId,
                                 HttpServletRequest request,
                                 HttpServletResponse response){
        // 清楚用户相关信息的cookie
        CookieUtils.deleteCookie(request, response, "user");

        // 用户退出登录，清除redis中user的会话信息
        redisOperator.del("redis_user_token:" + userId);

        // 用户退出登录，需要清空购物车
        CookieUtils.deleteCookie(request, response, "shopcart");

        return ServerResponse.createBySuccess();
    }

    private ServerResponse getServerResponse(HttpServletRequest request, HttpServletResponse response, Users userResult) {
        String uniqueToken = UUID.randomUUID().toString().trim();
        redisOperator.set("redis_user_token:" + userResult.getId(), uniqueToken);

        UserVO userVo = new UserVO();
        BeanUtils.copyProperties(userResult, userVo);
        userVo.setUserUniqueToken(uniqueToken);

        CookieUtils.setCookie(request, response, "user", JsonUtil.objToString(userVo), true);

        return ServerResponse.createBySuccess(userVo);
    }

    /**
     * 注册登录成功后，同步cookie和redis中的购物车数据
     *
     * @param userId
     * @param request
     * @param response
     */
    private void syncShopCartData(String userId, HttpServletRequest request, HttpServletResponse response){
        /**
         * 1. redis中无数据，如果cookie中的购物车为空，不做任何处理
         *                 如果cookie中的购物车不为空，存储到redis
         * 2. redis中有数据，如果cookie中的购物车为空，把redis的购物车数据覆盖到本地cookie
         *                 如果cookie中的购物车不为空，cookie中的某个商品在redis中存在，
         *                 则以cookie为主，删除redis中的数据，把cookie中的商品直接覆盖到redis（参考京东）
         * 3. 同步到redis中，覆盖本地cookie购物车的数据，保证本地购物车的数据是最新的
         */

        // 从redis中获取购物车数据
        String shopcartJsonRedis = redisOperator.get("shopcart:" + userId);

        // 从cookie中获取购物车数据
        String shopcartStrCookie = CookieUtils.getCookieValue(request, "shopcart", true);

        if (StringUtils.isBlank(shopcartJsonRedis)){
            // redis为空，cookie不为空，把cookie中的数据放到redis
            if (StringUtils.isNotBlank(shopcartStrCookie)){
                redisOperator.set("shopcart:" + userId, shopcartStrCookie);
            }
        } else {
            // redis不为空，cookie不为空，合并cookie和redis中的购物车的商品数据（同一商品则覆盖redis）
            if (StringUtils.isNotBlank(shopcartStrCookie)){
                /**
                 * 1. 已经存在的，把cookie中对应的数量覆盖到redis（参考京东）
                 * 2. 该项商品标记为待删除，统一放入一个待删除的list
                 * 3. 从cookie中清理所有的待删除list
                 * 4. 合并redis和cookie中的数据
                 * 5. 更新到redis和cookie
                 */

                List<ShopCartBO> shopcartListRedis = JsonUtil.jsonToList(shopcartJsonRedis, ShopCartBO.class);
                List<ShopCartBO> shopcartListCookie = JsonUtil.jsonToList(shopcartStrCookie, ShopCartBO.class);

                // 定义一个待删除的list
                List<ShopCartBO> pendingDeleteList = Lists.newArrayList();

                for (ShopCartBO redisShopCart : shopcartListRedis) {
                    String redisSpecId = redisShopCart.getSpecId();

                    for (ShopCartBO cookieShopCart : shopcartListCookie) {
                        String cookieSpecId = cookieShopCart.getSpecId();

                        if (StringUtils.equals(redisSpecId, cookieSpecId)){
                            // 覆盖购买数量，不累加，参考 京东
                            redisShopCart.setBuyCounts(cookieShopCart.getBuyCounts());
                            // 把cookieShopCart放入待删除列表，用于最后的删除与合并
                            pendingDeleteList.add(cookieShopCart);
                        }
                    }
                }

                // 从现有cookie中删除对应的覆盖过的商品数据
                shopcartListCookie.removeAll(pendingDeleteList);

                // 合并两个list
                shopcartListRedis.addAll(shopcartListCookie);
                // 更新到redis和cookie
                CookieUtils.setCookie(request, response, "shopcart", JsonUtil.objToString(shopcartListRedis), true);
                redisOperator.set("shopcart:" + userId, JsonUtil.objToString(shopcartListRedis));
            } else {
                // redis不为空，cookie为空，把redis中的数据覆盖到cookie
                CookieUtils.setCookie(request, response, "shopcart", shopcartJsonRedis, true);
            }
        }
    }
}

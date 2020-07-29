package com.sr.controller;

import com.sr.pojo.Users;
import com.sr.pojo.vo.UserVO;
import com.sr.pojo.vo.center.UsersVO;
import com.sr.service.IUserService;
import com.sr.utils.JsonUtil;
import com.sr.utils.MD5Util;
import com.sr.utils.RedisOperator;
import com.sr.utils.ServerResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author shirui
 * @date 2020/5/19
 */
@Controller
public class SSOController {

    private IUserService iUserService;

    private RedisOperator redisOperator;

    @Autowired
    public void setiUserService(IUserService iUserService) {
        this.iUserService = iUserService;
    }

    @Autowired
    public void setRedisOperator(RedisOperator redisOperator) {
        this.redisOperator = redisOperator;
    }

    @GetMapping("/login")
    public String login(String returnUrl,
                        Model model,
                        HttpServletRequest request,
                        HttpServletResponse response){

        model.addAttribute("returnUrl", returnUrl);

        String userTicket = getCookie(request, "cookie_user_ticket");

        boolean isVerify = verifyUserTicket(userTicket);
        if (isVerify){
            String tmpTicket = createTmpTicket();
            return StringUtils.join("redirect:", returnUrl, "?tmpTicket=", tmpTicket);
        }

        return "login";
    }

    /**
     * CAS统一登录接口
     *  目的：
     *      1. 登录后创建用户的全局会话                 uniqueToken
     *      2. 创建用户全局门票，用已表示在CAS端是否登录   userTicket
     *      3. 创建用户的临时票据，用于回调回传           tmpTicket
     * @param username
     * @param password
     * @param returnUrl
     * @param model
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @PostMapping("/doLogin")
    public String doLogin(String username,
                          String password,
                          String returnUrl,
                          Model model,
                          HttpServletRequest request,
                          HttpServletResponse response) throws Exception{

        model.addAttribute("returnUrl", returnUrl);

        // 0. 判断用户名和密码必须不为空
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)){
            model.addAttribute("errmsg", "用户名或密码不能为空");
            return "login";
        }

        // 1. 实现登录
        Users userResult = iUserService.queryUserForLogin(username, MD5Util.MD5EncodeUtf8(password));
        if (userResult == null){
            model.addAttribute("errmsg", "用户名或密码不正确");
            return "login";
        }

        // 2. 实现用户的redis会话
        String uniqueToken = UUID.randomUUID().toString().trim();
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userResult, userVO);
        userVO.setUserUniqueToken(uniqueToken);
        redisOperator.set("redis_user_token:" + userResult.getId(), JsonUtil.objToString(userVO));

        // 3. 生成ticket门票，全局门票，代表用户在CAS端登陆过
        String userTicket = UUID.randomUUID().toString().trim();

        // 3.1 用户全局门票需要放入CAS端的cookie中
        setCookie("cookie_user_ticket", userTicket, response);

        // 4. userTicket关联用户id，并且放入到redis中，代表这个用户有门票了，可以在各个景区游玩
        redisOperator.set("redis_user_ticket:" + userTicket, userResult.getId());

        // 5. 生成临时票据，回调到用户端网站，是由CAS端所签发的一个一次性的临时ticket
        String tmpTicket = createTmpTicket();

        /**
         * userTicket 用于表示用户在CAS端的一个登陆状态：已登录
         * tmpTicket 用于颁发给用户进行一次性的验证的票据，有时效性
         */

        /**
         * 例：
         *      我们去动物园玩耍，大门口买了一张统一的门票，这个就是CAS系统的全局门票和用户全局会话。
         *      动物园里有一些小的景点，需要凭你的门票去领取一次性的票据，有了这张票据以后就能去一些小的景点游玩了。
         *      这样的一个个的小景点其实就是我们这里所对应的一个个的站点。
         *      当我们使用完毕这张临时票据以后，就需要销毁。
         */

        return StringUtils.join("redirect:", returnUrl, "?tmpTicket=" + tmpTicket);
    }

    @PostMapping("/verifyTmpTicket")
    @ResponseBody
    public ServerResponse verifyTmpTicket(String tmpTicket,
                                          HttpServletRequest request,
                                          HttpServletResponse response){
        // 使用一次性临时票据来验证用户是否登录，如果登录过，把用户会话信息返回给站点
        // 使用完毕后，需要销毁临时票据
        String tmpTicketValue = redisOperator.get("redis_tmp_ticket:" + tmpTicket);
        if (StringUtils.isBlank(tmpTicketValue)){
            return ServerResponse.createByErrorMessage("用户票据异常");
        }

        // 0. 如果临时票据OK，则需要销毁，并且拿到CAS端cookie中的全局userTicket，以此再获取用户会话
        if (!StringUtils.equals(tmpTicketValue, MD5Util.MD5EncodeUtf8(tmpTicket))){
            return ServerResponse.createByErrorMessage("用户票据异常");
        } else{
            // 销毁临时票据
            redisOperator.del("redis_tmp_ticket:" + tmpTicket);
        }

        // 1. 验证并获取用户的userTicket
        String userTicket = getCookie(request, "cookie_user_ticket");
        String userId = redisOperator.get("redis_user_ticket:" + userTicket);
        if (StringUtils.isBlank(userId)){
            return ServerResponse.createByErrorMessage("用户票据异常");
        }

        // 2. 验证门票对应的user会话是否存在
        String userRedis = redisOperator.get("redis_user_token:" + userId);
        if (StringUtils.isBlank(userRedis)){
            return ServerResponse.createByErrorMessage("用户票据");
        }

        // 验证成功，返回ok，携带用户会话
        return ServerResponse.createBySuccess(JsonUtil.stringToObject(userRedis, UsersVO.class));
    }

    @PostMapping("/logout")
    @ResponseBody
    public ServerResponse logout(String userId,
                                 HttpServletRequest request,
                                 HttpServletResponse response){
        // 0. 获取CAS中的用户门票
        String userTicket = getCookie(request, "cookie_user_ticket");

        // 1. 清除userTicket票据，redis/cookie
        deleteCookie("cookie_user_ticket", response);
        redisOperator.del("redis_user_ticket:" + userTicket);

        // 2. 清除用户全局会话（分布式会话）
        redisOperator.del("redis_user_token:" + userId);

        return ServerResponse.createBySuccess();
    }

    /**
     * 校验CAS全局用户门票
     * @param userTicket
     * @return
     */
    private boolean verifyUserTicket(String userTicket){
        // 0. 验证CAS门票不能为空
        if (StringUtils.isBlank(userTicket)){
            return false;
        }

        // 1. 验证CAS门票是否有效
        String userId = redisOperator.get("redis_user_ticket:" + userTicket);
        if (StringUtils.isBlank(userId)){
            return false;
        }

        // 2. 验证门票对应的user会话是否存在
        String userRedis = redisOperator.get("redis_user_token:" + userId);
        if (StringUtils.isBlank(userRedis)){
            return false;
        }

        return true;
    }

    private String createTmpTicket(){
        String tmpTicket = UUID.randomUUID().toString().trim();
        redisOperator.set("redis_tmp_ticket:" + tmpTicket, MD5Util.MD5EncodeUtf8(tmpTicket), 600);

        return tmpTicket;
    }

    private void setCookie(String key,
                           String val,
                           HttpServletResponse response){
        Cookie cookie = new Cookie(key, val);
        cookie.setDomain("sso.com");
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private String getCookie(HttpServletRequest request, String key){
        Cookie[] cookies = request.getCookies();
        if (cookies == null || StringUtils.isBlank(key)){
            return null;
        }

        String cookieValue = null;
        for (Cookie cookie : cookies) {
            if (StringUtils.equals(key, cookie.getName())){
                cookieValue = cookie.getValue();
                break;
            }
        }

        return cookieValue;
    }

    private void deleteCookie(String key,
                              HttpServletResponse response){
        Cookie cookie = new Cookie(key, null);
        cookie.setDomain("sso.com");
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }
}

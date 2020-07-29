package com.sr.interceptor;

import com.sr.utils.JsonUtil;
import com.sr.utils.RedisOperator;
import com.sr.utils.ServerResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author shirui
 * @date 2020/5/11
 */
public class UserTokenInterceptor implements HandlerInterceptor {

    private RedisOperator redisOperator;

    @Autowired
    public void setRedisOperator(RedisOperator redisOperator) {
        this.redisOperator = redisOperator;
    }

    /**
     * 拦截请求，在访问controller调用前
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("headerUserId");
        String userToken = request.getHeader("headerUserToken");

        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)){
            String uniqueToken = redisOperator.get("redis_user_token:" + userId);
            if (StringUtils.isBlank(uniqueToken)){
                returnErrorResponse(response, "请登录");
                return false;
            }else {
                if (!StringUtils.equals(userToken, uniqueToken)){
                    returnErrorResponse(response, "账号在异地登录");
                    return false;
                }
            }
        } else{
            returnErrorResponse(response, "请登录");
            return false;
        }

        return true;
    }

    /**
     * 请求访问controller后，渲染视图前
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 请求访问controller后，渲染视图后
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    public void returnErrorResponse(HttpServletResponse response, String message){
        OutputStream outputStream = null;

        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            outputStream = response.getOutputStream();
            outputStream.write(JsonUtil.objToString(ServerResponse.createByErrorMessage(message)).getBytes("utf-8"));
            outputStream.flush();
        } catch(IOException e){
            e.printStackTrace();
        }finally {
            try{
                if (outputStream != null){
                    outputStream.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}

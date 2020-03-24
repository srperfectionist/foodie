package com.sr.controller;

import com.sr.pojo.Users;
import com.sr.pojo.bo.UserBO;
import com.sr.pojo.vo.UserVO;
import com.sr.service.IUserService;
import com.sr.utils.CookieUtils;
import com.sr.utils.JsonUtil;
import com.sr.utils.MD5Util;
import com.sr.utils.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    @Autowired
    public void setIUserService(IUserService iUserService){
        this.iUserService = iUserService;
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

        return getServerResponse(request, response, userResult);
    }

    @ApiOperation(value = "用户退出登录", notes = "用户退出登录", httpMethod = "POST")
    @PostMapping("/logout")
    public ServerResponse logout(@RequestParam String userId,
                                 HttpServletRequest request,
                                 HttpServletResponse response){
        // 清楚用户相关信息的cookie
        CookieUtils.deleteCookie(request, response, "user");

        return ServerResponse.createBySuccess();
    }
    private ServerResponse getServerResponse(HttpServletRequest request, HttpServletResponse response, Users userResult) {
        UserVO userVo = UserVO.builder().id(userResult.getId())
                .username(userResult.getUsername())
                .nickname(userResult.getNickname())
                .realname(userResult.getRealname())
                .face(userResult.getFace())
                .sex(userResult.getSex())
                .build();

        CookieUtils.setCookie(request, response, "user", JsonUtil.objToString(userVo), true);

        return ServerResponse.createBySuccess(userVo);
    }
}

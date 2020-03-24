package com.sr.controller.center;

import com.sr.pojo.Users;
import com.sr.service.center.ICenterUserService;
import com.sr.utils.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shirui
 * @date 2020/2/23
 */
@Api(value = "center - 用户中心", tags = {"用户中心展示的相关接口"})
@RestController
@RequestMapping("/center")
public class CenterController {

    private ICenterUserService iCenterUserService;

    @Autowired
    public void setiCenterUserService(ICenterUserService iCenterUserService) {
        this.iCenterUserService = iCenterUserService;
    }

    @ApiOperation(value = "获取用户信息", notes = "获取用户信息", httpMethod = "GET")
    @GetMapping("/userInfo")
    public ServerResponse userInfo(
            @ApiParam(name = "useId", value = "用户Id", required = true)
            @RequestParam("userId") String userId){
        Users users = iCenterUserService.queryUserInfo(userId);

        return ServerResponse.createBySuccess(users);
    }
}

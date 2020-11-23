package com.sr.controller;

import com.sr.pojo.UserAddress;
import com.sr.pojo.bo.AddressBO;
import com.sr.service.IAddressService;
import com.sr.utils.MobileEmailUtils;
import com.sr.utils.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author SR
 * @date 2020/1/14
 */
@Api(value = "地址相关", tags = {"地址相关的api接口"})
@RestController
@RequestMapping("/address")
public class AddressController {

    private IAddressService iAddressService;

    @Autowired
    public void setiAddressService(IAddressService iAddressService) {
        this.iAddressService = iAddressService;
    }

    @ApiOperation(value = "根据用户id查询收货地址列表", notes = "根据用户id查询收货地址列表", httpMethod = "POST")
    @PostMapping("/list")
    public ServerResponse list(@RequestParam String userId){
        if (StringUtils.isBlank(userId)){
            return ServerResponse.createByErrorMessage("用户Id不能为空");
        }

        List<UserAddress> userAddressList = iAddressService.queryAll(userId);

        return ServerResponse.createBySuccess(userAddressList);
    }

    @ApiOperation(value = "用户新增地址", notes = "用户新增地址", httpMethod = "POST")
    @PostMapping("/add")
    public ServerResponse add(@RequestBody AddressBO addressBO) {

        ServerResponse checkRes = checkAddress(addressBO);
        if (checkRes.getStatus() != 200) {
            return checkRes;
        }

        iAddressService.addNewUserAddress(addressBO);

        return ServerResponse.createBySuccess();
    }

    private ServerResponse checkAddress(AddressBO addressBO) {
        String receiver = addressBO.getReceiver();
        if (StringUtils.isBlank(receiver)) {
            return ServerResponse.createByErrorMessage("收货人不能为空");
        }

        if (receiver.length() > 12) {
            return ServerResponse.createByErrorMessage("收货人姓名不能太长");
        }

        String mobile = addressBO.getMobile();
        if (StringUtils.isBlank(mobile)) {
            return ServerResponse.createByErrorMessage("收货人手机号不能为空");
        }

        if (mobile.length() != 11) {
            return ServerResponse.createByErrorMessage("收货人手机号长度不正确");
        }

        boolean isMobileOk = MobileEmailUtils.checkMobileIsOk(mobile);
        if (!isMobileOk) {
            return ServerResponse.createByErrorMessage("收货人手机号格式不正确");
        }

        String province = addressBO.getProvince();
        String city = addressBO.getCity();
        String district = addressBO.getDistrict();
        String detail = addressBO.getDetail();
        if (StringUtils.isBlank(province) ||
                StringUtils.isBlank(city) ||
                StringUtils.isBlank(district) ||
                StringUtils.isBlank(detail)) {
            return ServerResponse.createByErrorMessage("收货地址信息不能为空");
        }

        return ServerResponse.createBySuccess();
    }

    @ApiOperation(value = "用户修改地址", notes = "用户修改地址", httpMethod = "POST")
    @PostMapping("/update")
    public ServerResponse update(@RequestBody AddressBO addressBO) {

        if (StringUtils.isBlank(addressBO.getAddressId())) {
            return ServerResponse.createByErrorMessage("修改地址错误：addressId不能为空");
        }

        ServerResponse checkRes = checkAddress(addressBO);
        if (checkRes.getStatus() != 200) {
            return checkRes;
        }

        iAddressService.updateUserAddress(addressBO);

        return ServerResponse.createBySuccess();
    }

    @ApiOperation(value = "用户删除地址", notes = "用户删除地址", httpMethod = "POST")
    @PostMapping("/delete")
    public ServerResponse delete(
            @RequestParam String userId,
            @RequestParam String addressId) {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(addressId)) {
            return ServerResponse.createByErrorMessage("");
        }

        iAddressService.deleteUserAddress(userId, addressId);
        return ServerResponse.createBySuccess();
    }

    @ApiOperation(value = "用户设置默认地址", notes = "用户设置默认地址", httpMethod = "POST")
    @PostMapping("/setDefault")
    public ServerResponse setDefault(
            @RequestParam String userId,
            @RequestParam String addressId) {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(addressId)) {
            return ServerResponse.createByErrorMessage("");
        }

        iAddressService.updateUserAddressToBeDefault(userId, addressId);
        return ServerResponse.createBySuccess();
    }
}

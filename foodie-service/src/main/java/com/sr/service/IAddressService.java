package com.sr.service;

import com.sr.pojo.UserAddress;
import com.sr.pojo.bo.AddressBO;

import java.util.List;

/**
 * @author SR
 * @date 2020/1/13
 */
public interface IAddressService {

    /**
     * 根据用户Id查询用户的收货地址列表
     * @param userId
     * @return
     */
    List<UserAddress> queryAll(String userId);

    /**
     * 用户新增地址
     * @param addressBo
     */
    void addNewUserAddress(AddressBO addressBo);

    /**
     * 修改用户地址
     * @param addressBo
     */
    void updateUserAddress(AddressBO addressBo);

    /**
     * 根据用户Id和地址Id，删除对应的用户地址信息
     * @param userId
     * @param addressId
     */
    void deleteUserAddress(String userId, String addressId);

    /**
     * 修改默认地址
     * @param userId
     * @param addressId
     */
    void updateUserAddressToBeDefault(String userId, String addressId);

    /**
     * 根据用户Id和地址Id，查询具体的用户地址对象信息
     * @param userId
     * @param addressId
     * @return
     */
    UserAddress queryUserAddress(String userId, String addressId);
}

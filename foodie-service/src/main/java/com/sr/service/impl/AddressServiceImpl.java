package com.sr.service.impl;

import com.sr.enums.YesOrNoEnum;
import com.sr.mapper.UserAddressMapper;
import com.sr.pojo.UserAddress;
import com.sr.pojo.bo.AddressBO;
import com.sr.service.IAddressService;
import org.apache.commons.collections.CollectionUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author SR
 * @date 2020/1/13
 */
@Service("iAddressService")
public class AddressServiceImpl implements IAddressService {

    private UserAddressMapper userAddressMapper;

    private Sid sid;

    @Autowired
    public void setUserAddressMapper(UserAddressMapper userAddressMapper) {
        this.userAddressMapper = userAddressMapper;
    }

    @Autowired
    public void setSid(Sid sid) {
        this.sid = sid;
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public List<UserAddress> queryAll(String userId) {
        UserAddress userAddress = UserAddress.builder().userId(userId).build();
        return userAddressMapper.select(userAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public void addNewUserAddress(AddressBO addressBo) {

        // 1. 判断当前用户是否存在地址，如果没有，则新增为“默认地址”
        Integer isDefault = 0;
        List<UserAddress> userAddressList = this.queryAll(addressBo.getUserId());
        if (CollectionUtils.isEmpty(userAddressList)){
            isDefault = 1;
        }

        // 2.保存地址到数据库
        String addressId = sid.nextShort();
        UserAddress userAddress = new UserAddress();
        BeanUtils.copyProperties(addressBo, userAddress);

        userAddress.setId(addressId);
        userAddress.setIsDefault(isDefault);
        userAddress.setCreatedTime(new Date());
        userAddress.setUpdatedTime(new Date());

        userAddressMapper.insert(userAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public void updateUserAddress(AddressBO addressBo) {
        UserAddress userAddress = new UserAddress();
        BeanUtils.copyProperties(addressBo, userAddress);

        userAddress.setId(addressBo.getAddressId());
        userAddress.setUpdatedTime(new Date());

        userAddressMapper.updateByPrimaryKeySelective(userAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public void deleteUserAddress(String userId, String addressId) {
        UserAddress userAddress = UserAddress.builder().id(addressId).userId(userId).build();
        userAddressMapper.delete(userAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public void updateUserAddressToBeDefault(String userId, String addressId) {
        // 1. 查询默认地址，设置为不默认
        UserAddress userAddress = UserAddress.builder().userId(userId).isDefault(YesOrNoEnum.YES.getType()).build();
        List<UserAddress> userAddressList = userAddressMapper.select(userAddress);

        userAddressList.forEach(ua -> {
            ua.setIsDefault(YesOrNoEnum.NO.getType());
            userAddressMapper.updateByPrimaryKeySelective(ua);
        });

        // 2. 根据地址Id修改为默认地址
        UserAddress defaultUserAddress = UserAddress.builder().id(addressId).userId(userId).isDefault(YesOrNoEnum.YES.getType()).updatedTime(new Date()).build();
        userAddressMapper.updateByPrimaryKeySelective(defaultUserAddress);
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public UserAddress queryUserAddress(String userId, String addressId) {
        UserAddress userAddress = UserAddress.builder().id(addressId).userId(userId).build();

        return userAddressMapper.selectOne(userAddress);
    }
}

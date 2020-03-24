package com.sr.service.impl;

import com.sr.enums.SexEnum;
import com.sr.mapper.UsersMapper;
import com.sr.pojo.Users;
import com.sr.pojo.bo.UserBO;
import com.sr.service.IUserService;
import com.sr.utils.DateUtil;
import com.sr.utils.MD5Util;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

/**
 * @author SR
 * @date 2019/11/19
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    private UsersMapper usersMapper;

    private Sid sid;

    private static final String USER_FACE = "http://122.152.205.72:88/group1/M00/00/05/CpoxxFw_8_qAIlFXAAAcIhVPdSg994.png";

    @Autowired
    private void setUsersMapper(UsersMapper usersMapper){
        this.usersMapper = usersMapper;
    }

    @Autowired
    public void setSid(Sid sid) {
        this.sid = sid;
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public boolean queryUsernameIsExist(String username) {
        Example userExample = new Example(Users.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andEqualTo("username", username);
        Users result = usersMapper.selectOneByExample(userExample);
        return result == null;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public Users creatUser(UserBO userBo) {
        String userId = sid.nextShort();

        Users user = new Users();
        user.setId(userId);
        user.setUsername(userBo.getUsername());
        user.setPassword(MD5Util.MD5EncodeUtf8(userBo.getPassword()));
        user.setNickname(userBo.getUsername());
        user.setFace(USER_FACE);
        user.setBirthday(DateUtil.strToDate("1900-01-01","yyyy-MM-dd"));
        user.setSex(SexEnum.SECRET.getType());
        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());

        usersMapper.insert(user);

        return user;
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public Users queryUserForLogin(String username, String password) {
        Example userExample = new Example(Users.class);
        Example.Criteria userCriteria = userExample.createCriteria();

        userCriteria.andEqualTo("username", username);
        userCriteria.andEqualTo("password", password);

        Users users = usersMapper.selectOneByExample(userExample);

        return users;
    }
}

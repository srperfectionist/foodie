package com.sr.service;

import com.sr.pojo.Users;
import com.sr.pojo.bo.UserBO;

/**
 * @author SR
 * @date 2019/11/19
 */
public interface IUserService {

    /**
     * 判断用户名是否存在
     * @param username
     * @return
     */
    boolean queryUsernameIsExist(String username);

    /**
     * 判断用户名是否存在
     * @param user
     * @return
     */
    Users creatUser(UserBO user);

    /**
     * 检索用户名和密码是否匹配，用于登录
     * @param username
     * @param password
     * @return
     */
    Users queryUserForLogin(String username, String password);
}

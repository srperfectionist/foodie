package com.sr.service.center;

import com.sr.pojo.Users;
import com.sr.pojo.bo.center.CenterUserB0;

/**
 * @author shirui
 * @date 2020/2/23
 */
public interface ICenterUserService {

    /**
     * 根据用户Id查询用户信息
     * @param userId
     * @return
     */
    Users  queryUserInfo(String userId);

    /**
     * 修改用户信息
     * @param userId
     * @param centerUserBo
     * @return
     */
    Users updateUserInfo(String userId, CenterUserB0 centerUserBo);

    /**
     * 用户头像更新
     * @param userId
     * @param faceUrl
     * @return
     */
    Users updateUserFace(String userId, String faceUrl);
}

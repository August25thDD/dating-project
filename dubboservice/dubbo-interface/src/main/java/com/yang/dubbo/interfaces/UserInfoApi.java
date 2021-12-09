package com.yang.dubbo.interfaces;

import com.yang.commons.pojo.UserInfo;

import java.util.List;
import java.util.Map;

public interface UserInfoApi {

    /**
     * 添加用户到数据库中
      * @param userInfo
     */
    void addUserInfo(UserInfo userInfo);

    /**
     * 根据用户id更新用户注册的头像到数据库
     * @param uid
     * @param pictureURL
     */
    void updataLog(Long uid, String pictureURL);

    // 根据user_id字段查询一个userinfo
    UserInfo findUserInfoByUserId(Long uid);

    // 根据一个user_id集合查询一个List<UserInfo>
    Map<Long, UserInfo> findUserInfoListByUserIds(UserInfo userInfo, List<Long> userIds);

    /**
     * 根据一个user_id集合查询一个List<UserInfo>
     * @param userIds
     * @return
     */
    Map<Long, UserInfo> findUserInfoListByUserIds(List<Long> userIds);
}

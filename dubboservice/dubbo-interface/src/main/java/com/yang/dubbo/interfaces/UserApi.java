package com.yang.dubbo.interfaces;

import com.yang.commons.pojo.User;

public interface UserApi {

    /**
     * 判断一个用户数是否是新用户
     * @param phoneNum
     * @return
     */
    User findUserByPhoneNum(String phoneNum);

    /**
     * 注册新用户
     * @param user
     * @return
     */
    User addUser(User user);
}

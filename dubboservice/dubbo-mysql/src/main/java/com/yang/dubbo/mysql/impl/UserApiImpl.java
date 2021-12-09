package com.yang.dubbo.mysql.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import com.yang.commons.pojo.User;
import com.yang.dubbo.interfaces.UserApi;
import com.yang.dubbo.mysql.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @program: my-tanhua
 * @description:
 * @author: Mr.Yang
 * @create: 2021-12-03 20:59
 **/
@Service(timeout = 5000)
public class UserApiImpl implements UserApi {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User findUserByPhoneNum(String phoneNum) {
        return userMapper.selectOne(Wrappers.lambdaQuery(User.class)
                .eq(User::getMobile, phoneNum)
        );
    }

    @Override
    public User addUser(User user) {

        userMapper.insert(user);
//        返回过去的User是有Id的
        return user;
    }
}

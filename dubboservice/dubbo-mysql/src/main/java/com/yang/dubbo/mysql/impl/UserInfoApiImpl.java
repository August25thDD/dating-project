package com.yang.dubbo.mysql.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yang.commons.pojo.UserInfo;
import com.yang.dubbo.interfaces.UserInfoApi;
import com.yang.dubbo.mysql.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @program: my-tanhua
 * @description:
 * @author: Mr.Yang
 * @create: 2021-12-04 20:12
 **/
@Service(timeout = 5000)
public class UserInfoApiImpl implements UserInfoApi {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public void addUserInfo(UserInfo userInfo) {
        userInfoMapper.insert(userInfo);
    }

    @Override
    public void updataLog(Long uid, String pictureURL) {
        userInfoMapper.update(UserInfo.builder().logo(pictureURL).build(),
                Wrappers.lambdaQuery(UserInfo.class).eq(UserInfo::getUserId, uid));;
    }

    @Override
    public UserInfo findUserInfoByUserId(Long uid) {
        return userInfoMapper.selectOne(Wrappers.lambdaQuery(UserInfo.class)
                .eq(UserInfo::getUserId, uid));
    }

    @Override
    public Map<Long, UserInfo> findUserInfoListByUserIds(UserInfo userInfo, List<Long> userIds) {
        List<UserInfo> userInfos = userInfoMapper.selectList(Wrappers.lambdaQuery(UserInfo.class)
                .in(UserInfo::getUserId, userIds)
                .eq(ObjectUtil.isNotNull(userInfo.getSex()), UserInfo::getSex, userInfo.getSex())
                .eq(ObjectUtil.isNotNull(userInfo.getCity()), UserInfo::getCity, userInfo.getCity())
                .gt(ObjectUtil.isNotNull(userInfo.getAge()), UserInfo::getAge, userInfo.getAge())
        );

        Map<Long, UserInfo> infoMap = CollUtil.fieldValueMap(userInfos, "userId");
        return infoMap;
    }


    @Override
    public Map<Long, UserInfo> findUserInfoListByUserIds(List<Long> userIds) {
        List<UserInfo> userInfos = userInfoMapper.selectList(Wrappers.lambdaQuery(UserInfo.class)
                .in(UserInfo::getUserId, userIds)
        );
        Map<Long, UserInfo> infoMap = CollUtil.fieldValueMap(userInfos, "userId");
        return infoMap;
    }
}

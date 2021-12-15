package com.yang.appserver.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yang.appserver.interceptor.UserThreadLocal;
import com.yang.commons.pojo.HuanxinUser;
import com.yang.dubbo.interfaces.HuanxinUserApi;
import com.yang.dubbo.interfaces.HuanxinUserApi;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @项目名称: tanhua-53
 * @包名: com.yang.appserver.service
 * @作者: 李云飞
 * @日期: 2021-12-09
 */

@Service
public class HuanXinService {

    @Reference
    private HuanxinUserApi huanxinUserApi;


    // 根据用户ID查询环信的账号和密码返回
    public Map<String, String> queryHuanXinUser() {
        HuanxinUser huanxinUser = huanxinUserApi.findHXUserByUserId(UserThreadLocal.getUserId());
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("username", huanxinUser.getUsername());
        hashMap.put("password", huanxinUser.getPassword());
        return hashMap;
    }
}

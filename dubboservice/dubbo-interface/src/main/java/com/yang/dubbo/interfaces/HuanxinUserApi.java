package com.yang.dubbo.interfaces;


import com.yang.commons.pojo.HuanxinUser;

/**
 * @项目名称: tanhua-53
 * @包名: com.itheima.dubbo.interfaces
 * @作者: 李云飞
 * @日期: 2021-12-03
 * <p>
 * User接口
 */
public interface HuanxinUserApi {
    /**
     * 根据手机号码查询一个用户
     *
     * @param userId
     * @return
     */
    HuanxinUser findHXUserByUserId(Long userId);

    /**
     * 新用户注册
     * 添加一个User到tb_user表中
     *
     * @param hxuser
     * @return
     */
    void addUser(HuanxinUser hxuser);

}

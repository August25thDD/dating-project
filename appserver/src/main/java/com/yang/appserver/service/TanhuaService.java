package com.yang.appserver.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.yang.appserver.exception.ErrorResult;
import com.yang.appserver.exception.MyException;
import com.yang.appserver.interceptor.UserThreadLocal;
import com.yang.commons.pojo.RecommendUser;
import com.yang.commons.pojo.UserInfo;
import com.yang.commons.vo.PageResult;
import com.yang.commons.vo.TodayBest;
import com.yang.dubbo.interfaces.RecommendUserApi;
import com.yang.dubbo.interfaces.UserInfoApi;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @program: my-tanhua
 * @description:
 * @author: Mr.Yang
 * @create: 2021-12-06 22:33
 **/
@Service
public class TanhuaService {

    @Reference
    private RecommendUserApi recommendUserApi;

    @Reference
    private UserInfoApi userInfoApi;

    /**
     * 查询今日佳人
     *
     * @return
     */
    public TodayBest todayBest() {
        // 1.解析Token 拿到登录人的ID  在拦截器中已经解析过了
        // 2.根据ID去查询最高分  Dubbo调用
        RecommendUser recommendUser = recommendUserApi.queryWithMaxScore(UserThreadLocal.getUserId());
        if (ObjectUtil.isNull(recommendUser)) {
            throw new MyException(ErrorResult.recommendUserIsEmpty());
        }
        // 3.根据最高分的人的id查询tb_user_info
        UserInfo userInfo = userInfoApi.findUserInfoByUserId(recommendUser.getUserId());

        // 4.把第2步和第3步的结果集 合并成一个VO
        TodayBest todayBest = TodayBest.init(userInfo, recommendUser);
        return todayBest;

    }

    /**
     * 查询首页推荐列表
     *
     * @param params
     * @return
     */
    public PageResult recommendationList(Map<String, Object> params) {
// 1.解析Token 拿到登录人的ID  在拦截器中已经解析过了
        // 2.根据ID去分页查询给这个用户推荐的所有用户
        PageResult pageResult = recommendUserApi.queryRecommendUserList(MapUtil.getInt(params, "page", 1),
                MapUtil.getInt(params, "pagesize", 5), UserThreadLocal.getUserId());
        List<RecommendUser> items = (List<RecommendUser>) pageResult.getItems();
        if (CollUtil.isEmpty(items)) {
            throw new MyException(ErrorResult.recommendUserIsEmpty());
        }
        // 3.从第2步的结果集中来提取userId 去查询tb_user_info
        List<Long> userIds = CollUtil.getFieldValues(items, "userId", Long.class);
        // 调用Dubbo去查询List<UserInfo>
        UserInfo userInfo = new UserInfo();
        userInfo.setSex(MapUtil.getStr(params, "gender"));
        userInfo.setCity(MapUtil.getStr(params, "city"));
        userInfo.setAge(MapUtil.getInt(params, "age"));

        Map<Long, UserInfo> userInfoMap = userInfoApi.findUserInfoListByUserIds(userInfo, userIds);

        // 把第2步和第3步的结果集 合并成一个VO  10个  25-30   60
        List<TodayBest> voList = new ArrayList<>();
        if (MapUtil.isEmpty(userInfoMap)) {
            pageResult.setItems(Collections.emptyList());
            return pageResult;
        }

        for (RecommendUser recommendUser : items) {
            // 分页 MongoDB --> 10条数据
            // 10个ID tb_user_info  筛选条件 没有满足  3个结果
            TodayBest best = TodayBest.init(userInfoMap.get(recommendUser.getUserId()), recommendUser);
            if (ObjectUtil.isNotNull(best)) {
                voList.add(best);
            }
        }
        pageResult.setItems(voList);
        return pageResult;

    }

}

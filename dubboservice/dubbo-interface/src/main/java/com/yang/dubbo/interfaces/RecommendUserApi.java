package com.yang.dubbo.interfaces;

import com.yang.commons.pojo.RecommendUser;
import com.yang.commons.vo.PageResult;

public interface RecommendUserApi {

    // 根据一个用户的id查询给他推荐的那个最高分的人
    RecommendUser queryWithMaxScore(Long toUserId);

    // 分页查询
    PageResult queryRecommendUserList(Integer page, Integer pagesize, Long toUserId);


    RecommendUser queryScoreByUserId(Long userId, Long userId1);

}
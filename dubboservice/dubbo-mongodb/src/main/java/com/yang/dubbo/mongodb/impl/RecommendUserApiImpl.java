package com.yang.dubbo.mongodb.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yang.commons.pojo.RecommendUser;
import com.yang.commons.vo.PageResult;
import com.yang.dubbo.interfaces.RecommendUserApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @program: my-tanhua
 * @description:
 * @author: Mr.Yang
 * @create: 2021-12-06 22:40
 **/

@Service
public class RecommendUserApiImpl implements RecommendUserApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    //查询今日佳人
    @Override
    public RecommendUser queryWithMaxScore(Long toUserId) {
        RecommendUser recommendUser = mongoTemplate.findOne(Query.query(Criteria.where("toUserId").is(toUserId))
                        .with(Sort.by(Sort.Order.desc("score")))
                        .with(PageRequest.of(0, 1))
                , RecommendUser.class
        );

        return recommendUser;
    }

    @Override
    public PageResult queryRecommendUserList(Integer page, Integer pagesize, Long toUserId) {
        // 在实际开发中如果有些需求 是模棱两可的  怎么做都行
        // 我们要去咨询产品 确定一个方案
        List<RecommendUser> recommendUsers = mongoTemplate.find(Query.query(Criteria.where("toUserId").is(toUserId))
                        .with(Sort.by(Sort.Order.desc("score")))
                        .with(PageRequest.of(page - 1, pagesize))
                , RecommendUser.class
        );
        // 凡是涉及到分页的查询
        // 我们一般都要把分页相关参数都返回
        // 当前页码  每页显示的条数 每页的数据 总的页码 总的条数

        long counts = mongoTemplate.count(new Query(), RecommendUser.class);
        return new PageResult(page, pagesize, counts, recommendUsers);
    }
}

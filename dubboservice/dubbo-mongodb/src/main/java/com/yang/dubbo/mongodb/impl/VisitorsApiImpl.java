package com.yang.dubbo.mongodb.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.dubbo.config.annotation.Service;
import com.yang.commons.pojo.Visitors;
import com.yang.dubbo.interfaces.VisitorsApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@Service
public class VisitorsApiImpl implements VisitorsApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public String saveVisitor(Visitors visitors) {
        Visitors visitor = mongoTemplate.save(visitors);
        String visitorId = visitor.getId().toHexString();
        return visitorId;
    }

    @Override
    public List<Visitors> queryMyVisitor(Long userId, Long lastQueryDate) {
        if (ObjectUtil.isEmpty(lastQueryDate)) {
            // 之前没有查询过 取最近来访者前5条
            return mongoTemplate.find(Query.query(Criteria.where("userId").is(userId))
                            .with(Sort.by(Sort.Order.desc("date")))
                            .with(PageRequest.of(0, 5))
                    , Visitors.class);
        }
        // 之前查询过来访者  取之前来访时间之后的前5条
        return mongoTemplate.find(Query.query(Criteria.where("userId").is(userId)
                        .and("date").gt(lastQueryDate))
                        .with(Sort.by(Sort.Order.desc("date")))
                        .with(PageRequest.of(0, 5))
                , Visitors.class);
    }

    // 分页查询最近来访的人
    @Override
    public List<Visitors> topVisitor(Long userId, Integer page, Integer pageSize) {
        List<Visitors> visitors = mongoTemplate.find(Query.query(Criteria.where("userId").is(userId))
                        .with(Sort.by(Sort.Order.desc("date")))
                        .with(PageRequest.of(page - 1, pageSize))
                , Visitors.class);
        /*for (Visitors visitor : visitors) {
            // 查询缘分值
            RecommendUser one = mongoTemplate.findOne(Query.query(Criteria.where("toUserId").is(userId)
                    .and("userId").is(visitor.getUserId())), RecommendUser.class);
            visitor.setScore(90d);
            if (!ObjectUtil.isEmpty(one)) {
                visitor.setScore(one.getScore());
            }
        }*/
        return visitors;
    }
}
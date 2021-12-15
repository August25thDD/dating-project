package com.yang.dubbo.mongodb.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.alibaba.dubbo.config.annotation.Service;
import com.yang.commons.pojo.Album;
import com.yang.commons.pojo.Publish;
import com.yang.commons.pojo.TimeLine;
import com.yang.dubbo.interfaces.PublishApi;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@Service
public class PublishApiImpl implements PublishApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public String publish(Publish publish) {
        // 直接往发布表中添加一条记录就可以了
        Publish pub = mongoTemplate.save(publish);
        return pub.getId().toHexString();
    }

    @Override
    public List<Publish> findFriendMovements(Integer page, Integer pagesize, Long userId) {
        // 查询自己的时间线表
        List<TimeLine> timeLines = mongoTemplate.find(new Query()
                        .with(Sort.by(Sort.Order.desc("date")))
                        .with(PageRequest.of(page - 1, pagesize)), TimeLine.class, "quanzi_time_line_" + userId);
//        获取圈子id的集合
        List<ObjectId> publishIds = CollUtil.getFieldValues(timeLines, "publishId", ObjectId.class);
        // 根据上面的圈子id去查询发布表
        List<Publish> publishList = mongoTemplate.find(Query.query(Criteria.where("id").in(publishIds)), Publish.class);
        return publishList;
    }

    @Override
    public List<Publish> findMovementsByPids(List<Long> pids) {
        // 从MongoDB去查询
        return mongoTemplate.find(Query.query(Criteria.where("pid").in(pids)),Publish.class);
    }

    @Override
    public List<Publish> randomMovements(Integer pagesize) {
        // 从MongoDB中随机抽样数据
        AggregationResults<Publish> aggregationResults = mongoTemplate.aggregate(Aggregation.newAggregation(Publish.class, Aggregation.sample(Convert.toLong(pagesize))),
                Publish.class);
        return aggregationResults.getMappedResults();
    }

    /**
     *
     * @param id 动态id
     * @return
     */
    @Override
    public Publish queryPublishById(String id) {
        return mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)), Publish.class);
    }

    @Override
    public List<Publish> findMovementsByUserId(Integer page, Integer pagesize, Long userId) {
        // 先查询这个人的相册表
        List<Album> albumList = mongoTemplate.find(new Query()
                        .with(Sort.by(Sort.Order.desc("created")))
                        .with(PageRequest.of(page - 1, pagesize))
                , Album.class, "quanzi_album_" + userId);
        // 提取圈子的ID
        List<Object> publishIds = CollUtil.getFieldValues(albumList, "publishId");
        // 再根据圈子的id查询圈子的发布表
        return mongoTemplate.find(Query.query(Criteria.where("id").in(publishIds)), Publish.class);
    }
}

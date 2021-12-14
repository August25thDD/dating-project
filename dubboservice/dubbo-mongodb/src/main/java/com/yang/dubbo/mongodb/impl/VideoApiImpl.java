package com.yang.dubbo.mongodb.impl;


import cn.hutool.core.convert.Convert;
import com.alibaba.dubbo.config.annotation.Service;
import com.yang.commons.pojo.FollowUser;
import com.yang.commons.pojo.Video;
import com.yang.commons.vo.PageResult;
import com.yang.dubbo.interfaces.VideoApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@Service
public class VideoApiImpl implements VideoApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    // 保存小视频
    @Override
    public String saveVideo(Video video) {
        // 需求  保存朋友圈-->生成的ID  朋友圈ID 保存到相册表  朋友的时间线表
        return mongoTemplate.save(video).getId().toHexString();
    }

    // 查询MongoDB中的小视频列表
    @Override
    public PageResult queryVideoList(Long userId, Integer page, Integer pageSize) {

        return null;
    }

    @Override
    public Video queryVideoById(String videoId) {
        return mongoTemplate.findOne(Query.query(Criteria.where("id").is(videoId)), Video.class);
    }

    // 关注主播
    @Override
    public void followUser(FollowUser followUser) {
        mongoTemplate.save(followUser);
    }

    @Override
    public void disFollowUser(Long userId, Long followUserId) {
        mongoTemplate.remove(Query.query(Criteria.where("userId").is(userId)
                .and("followUserId").is(followUserId)), FollowUser.class);
    }

    @Override
    public Boolean isFollowUser(Long userId, Long followUserId) {
        return null;
    }

    // 随机从video表中抽取指定条数的小视频数据
    @Override
    public List<Video> randomVideos(Integer pageSize) {
        // 从MongoDB中随机抽样数据
        AggregationResults<Video> aggregationResults = mongoTemplate.aggregate(Aggregation.newAggregation(Video.class,
                Aggregation.sample(Convert.toLong(pageSize))),
                Video.class);
        List<Video> mappedResults = aggregationResults.getMappedResults();
        return mappedResults;
    }

    // 根据一个vid的集合去查询video对象集合
    @Override
    public List<Video> findVideosByVids(List<Long> toList) {
        return mongoTemplate.find(Query.query(Criteria.where("vid").in(toList)), Video.class);
    }
}

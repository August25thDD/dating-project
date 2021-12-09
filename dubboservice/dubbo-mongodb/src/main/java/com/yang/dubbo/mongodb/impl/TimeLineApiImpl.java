package com.yang.dubbo.mongodb.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.dubbo.config.annotation.Service;
import com.yang.commons.pojo.TimeLine;
import com.yang.commons.pojo.Users;
import com.yang.dubbo.interfaces.TimeLineApi;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

@Service
public class TimeLineApiImpl implements TimeLineApi {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    // 在哪个方法上添加这个注解 那么这个方法就会被自动的放到子线程中执行
    @Async
    public void saveToFriendTimeLine(String publishId, Long userid) {
        // 查询自己的好友表 拿到好友的id
        List<Users> usersList = mongoTemplate.find(Query.query(Criteria.where("userId").is(userid)), Users.class);
        // 从上面的结果集中提取friendId
        List<Long> friendIdList = CollUtil.getFieldValues(usersList, "friendId", Long.class);
        // 根据好友的id操作他们的时间线表
        // 拼凑一个时间线表对象
        TimeLine timeLine = new TimeLine();
        timeLine.setId(new ObjectId());
        timeLine.setUserId(userid);
        timeLine.setPublishId(new ObjectId(publishId));
        timeLine.setDate(System.currentTimeMillis());
        for (Long fid : friendIdList) {
            // 找到好友的时间线表 插入进去
            mongoTemplate.save(timeLine, "quanzi_time_line_" + fid);
        }
    }


}

package com.yang.appserver.service;

import com.yang.commons.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @项目名称: tanhua-53
 * @包名: com.itheima.appserver.service
 * @作者: 李云飞
 * @日期: 2021-12-06
 */
@Service
public class IdService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 生成唯一不重复的id
     * @param idType 生成的redis的key字段类型:PUBLISH&&VEDIO
     * @return 生成的id
     */
    public Long createId(String idType) {

        // 对圈子生成一个id  PUBLISH
        // TANHUA_UNIT_ID_PUBLISH   123

        // 对圈子生成一个id  VEDIO
        // TANHUA_UNIT_ID_VEDIO   3

        String key = Constants.TANHUA_UNIT_ID + idType.toString();
        // 对Redis中的key  increment 做自增长
        return this.redisTemplate.opsForValue().increment(key);
    }
}

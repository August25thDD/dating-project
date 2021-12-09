package com.yang.appserver.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yang.dubbo.interfaces.TimeLineApi;
import org.springframework.stereotype.Service;

/**
 * @项目名称: tanhua-53
 * @包名: com.itheima.appserver.service
 * @作者: 李云飞
 * @日期: 2021-12-06
 */

@Service
public class TimeLineService {
    @Reference
    private TimeLineApi timeLineApi;

    public void saveTimeLine(Long userId, String id) {
        timeLineApi.saveToFriendTimeLine(id, userId);
    }
}

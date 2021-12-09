package com.yang.dubbo.interfaces;

import com.yang.commons.pojo.Publish;

import java.util.List;

/**
 * @项目名称: hztanhua
 * @包名: com.itheima.dubbo.interfaces
 * @作者: 李云飞
 * @日期: 2021-12-06
 */
public interface PublishApi {

    /**
     * 发布朋友圈
     *
     * @param publish
     * @return
     */
    String publish(Publish publish);

    /**
     * 分页查询自己好友的朋友圈列表
     *
     * @param page
     * @param pagesize
     * @param userId
     * @return
     */
    List<Publish> findFriendMovements(Integer page, Integer pagesize, Long userId);

    /**
     * 查询推荐的朋友圈列表
     *
     * @param pids
     * @return
     */
    List<Publish> findMovementsByPids(List<Long> pids);

    /**
     * 随机生成一些朋友圈列表数据
     *
     * @param pagesize
     * @return
     */
    List<Publish> randomMovements(Integer pagesize);

}

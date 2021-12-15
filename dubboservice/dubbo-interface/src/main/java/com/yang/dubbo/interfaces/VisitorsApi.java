package com.yang.dubbo.interfaces;

import com.yang.commons.pojo.Visitors;

import java.util.List;

public interface VisitorsApi {

    /**
     * 保存访客数据
     *
     * @return
     */
    String saveVisitor(Visitors visitors);

    /**
     * 查询我的访客数据，存在2种情况：
     * 1. 我没有看过我的访客数据，返回前5个访客信息
     * 2. 之前看过我的访客，从上一次查看的时间点往后查询5个访客数据
     *
     * @param userId
     * @return
     */
    List<Visitors> queryMyVisitor(Long userId, Long lastQueryDate);

    /**
     * 按照时间倒序排序，查询最近的访客信息
     *
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    List<Visitors> topVisitor(Long userId, Integer page, Integer pageSize);


}
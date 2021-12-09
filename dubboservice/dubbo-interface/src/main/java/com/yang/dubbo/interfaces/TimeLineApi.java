package com.yang.dubbo.interfaces;

/**
 * @项目名称: hztanhua
 * @包名: com.itheima.dubbo.interfaces
 * @作者: 李云飞
 * @日期: 2021-12-06
 */
public interface TimeLineApi {
    /**
     * 保存到自己的好友的时间线表中
     * 我(登录人)发布圈子  -->  要往我的好友的时间线表中去写数据
     * @param publishId  刚刚发布表中返回的那个ID
     * @param userid     我的ID(登录人的ID)
     */
    void saveToFriendTimeLine(String publishId,Long userid);
}

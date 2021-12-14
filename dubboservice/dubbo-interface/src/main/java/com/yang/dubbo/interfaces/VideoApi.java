package com.yang.dubbo.interfaces;

import com.yang.commons.pojo.FollowUser;
import com.yang.commons.pojo.Video;
import com.yang.commons.vo.PageResult;

import java.util.List;

/**
 * @项目名称: tanhua-53
 * @包名: com.yang.dubbo.interfaces
 * @作者: 李云飞
 * @日期: 2021-12-09
 */
public interface VideoApi {

    /**
     * 保存小视频
     *
     * @param video
     * @return 保存成功后，返回视频id
     */
    String saveVideo(Video video);

    /**
     * 分页查询小视频列表，按照时间倒序排序
     *
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    PageResult queryVideoList(Long userId, Integer page, Integer pageSize);

    /**
     * 根据id查询视频对象
     *
     * @param videoId 小视频id
     * @return
     */
    Video queryVideoById(String videoId);

    /**
     * 关注用户
     *
     * @param userId       当前用户
     * @param followUserId 关注的目标用户
     * @return
     */
    void followUser(FollowUser followUser);

    /**
     * 取消关注用户
     *
     * @param userId       当前用户
     * @param followUserId 关注的目标用户
     * @return
     */
    void disFollowUser(Long userId, Long followUserId);

    /**
     * 查询用户是否关注某个用户
     *
     * @param userId       当前用户
     * @param followUserId 关注的目标用户
     * @return
     */
    Boolean isFollowUser(Long userId, Long followUserId);

    List<Video> randomVideos(Integer pageSize);

    List<Video> findVideosByVids(List<Long> toList);
}
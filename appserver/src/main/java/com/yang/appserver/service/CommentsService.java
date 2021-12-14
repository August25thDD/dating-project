package com.yang.appserver.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.yang.appserver.interceptor.UserThreadLocal;
import com.yang.commons.constants.Constants;
import com.yang.commons.enums.CommentType;
import com.yang.commons.pojo.Comment;
import com.yang.commons.pojo.Publish;
import com.yang.commons.pojo.UserInfo;
import com.yang.commons.pojo.Video;
import com.yang.commons.vo.CommentVo;
import com.yang.commons.vo.PageResult;
import com.yang.dubbo.interfaces.CommentApi;
import com.yang.dubbo.interfaces.PublishApi;
import com.yang.dubbo.interfaces.UserInfoApi;
import com.yang.dubbo.interfaces.VideoApi;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @program: my-tanhua
 * @description:
 * @author: Mr.Yang
 * @create: 2021-12-09 21:19
 **/

@Service
public class CommentsService {

    @Reference
    private CommentApi commentApi;

    @Reference
    private PublishApi publishApi;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Reference
    private UserInfoApi userInfoApi;

    @Reference
    private VideoApi videoApi;

    /**
     * 对圈子点赞
     *
     * @param publishId
     * @return
     */
    public Long likeComment(String publishId) {
        // 往MongoDB中插入一条点赞数据
        Long likeCount = publicLikeComment(publishId);
        // 把当前这个圈子的点赞数给保存到redis中
        redisTemplate.opsForHash().put(Constants.MOVEMENTS_INTERACT_KEY + publishId, Constants.MOVEMENT_LIKE_HASHKEY, likeCount.toString());
        // 把自己对这个圈子点赞的标记存储到redis中
        redisTemplate.opsForHash().put(Constants.MOVEMENTS_INTERACT_KEY + publishId,
                Constants.MOVEMENT_ISLIKE_HASHKEY + UserThreadLocal.getUserId(), "1");

        return likeCount;
    }

    /**
     * 取消赞
     * @param  publishId
     * @return
     */
    public Long disLikeComment(String publishId) {
        Long disLikeCount = commentApi.unLikeComment(UserThreadLocal.getUserId(), new ObjectId(publishId));
        // 把当前这个圈子的点赞数给保存到redis中
        redisTemplate.opsForHash().put(Constants.MOVEMENTS_INTERACT_KEY + publishId,
                Constants.MOVEMENT_LIKE_HASHKEY, disLikeCount.toString());
        // 把自己对这个圈子点赞的标记存储到redis中
        redisTemplate.opsForHash().delete(Constants.MOVEMENTS_INTERACT_KEY + publishId,
                Constants.MOVEMENT_ISLIKE_HASHKEY + UserThreadLocal.getUserId());
        return disLikeCount;
    }

    /**
     *
     * 喜欢圈子
     * @param publishId
     * @return
     */
    public Long loveComment(String publishId) {
        Long loveCount = publicLoveComment(publishId);
        // 把当前这个圈子的喜欢数给保存到redis中
        redisTemplate.opsForHash().put(Constants.MOVEMENTS_INTERACT_KEY + publishId, Constants.MOVEMENT_LOVE_HASHKEY, loveCount.toString());
        // 把自己对这个圈子点赞的标记存储到redis中
        redisTemplate.opsForHash().put(Constants.MOVEMENTS_INTERACT_KEY + publishId,
                Constants.MOVEMENT_ISLOVE_HASHKEY + UserThreadLocal.getUserId(), "1");

        return loveCount;
    }

    /**
     * 取消喜欢
     * @param publishId
     * @return
     */
    public Long unloveComment(String publishId) {
        Long unLoveCount = commentApi.unLoveComment(UserThreadLocal.getUserId(), new ObjectId(publishId));
        // 把当前这个圈子的点赞数给保存到redis中
        redisTemplate.opsForHash().put(Constants.MOVEMENTS_INTERACT_KEY + publishId, Constants.MOVEMENT_LIKE_HASHKEY, unLoveCount.toString());
        // 把自己对这个圈子点赞的标记存储到redis中
        redisTemplate.opsForHash().delete(Constants.MOVEMENTS_INTERACT_KEY + publishId, Constants.MOVEMENT_ISLIKE_HASHKEY + UserThreadLocal.getUserId());
        return unLoveCount;
    }

    /**
     * 查询评论列表
     * @param publishId
     * @param page
     * @param pageSize
     * @return
     */
    public PageResult queryCommentsList(String publishId, Integer page, Integer pageSize) {

        PageResult pageResult = commentApi.queryCommentList(new ObjectId(publishId), page, pageSize);
        if (CollUtil.isEmpty(pageResult.getItems())) {
            return pageResult;
        }
        // 从评论列表中提取发布评论人的ID
        List<Long> userIds = CollUtil.getFieldValues(pageResult.getItems(), "userId", Long.class);
        // 根据这个发布人的id集合去查询tb_user_info
        Map<Long, UserInfo> userInfoList = userInfoApi.findUserInfoListByUserIds(userIds);

        List<CommentVo> voList = new ArrayList<>();
        for (Object item : pageResult.getItems()) {
            Comment comment = (Comment) item;
            // 拼凑vo对象
            CommentVo commentVo = new CommentVo();
            // 评论的ida
            commentVo.setId(comment.getId().toHexString());
            // 评论的内容
            commentVo.setContent(comment.getContent());
            // 评论的时间
            commentVo.setCreateDate(DateUtil.date(comment.getCreated()).toString("HH:mm"));

            // 根据评论发布人的id找到发布人的详情
            UserInfo userInfo = userInfoList.get(comment.getUserId());
            // 发布评论人的头像
            commentVo.setAvatar(userInfo.getLogo());
            // 发布评论人的昵称
            commentVo.setNickname(userInfo.getNickName());

            // 评论的点赞数量
            Long likeCount = Convert.toLong(redisTemplate.opsForHash().get(Constants.MOVEMENTS_INTERACT_KEY + comment.getId(),
                    Constants.MOVEMENT_LIKE_HASHKEY));
            // 说明当前这个圈子没有人点赞 或者有人点赞但是Redis中没值
            if (ObjectUtil.isNull(likeCount)) {
                // 从MongoDB中进行查询
                likeCount = commentApi.queryLikeCount(comment.getId());
                // 把查询到数据给保存到Redis中
                redisTemplate.opsForHash().put(Constants.MOVEMENTS_INTERACT_KEY + comment.getId(),
                        Constants.MOVEMENT_LIKE_HASHKEY, likeCount.toString());
            }
            commentVo.setLikeCount(Convert.toInt(likeCount));

            // 是否点赞
            Boolean isLike = redisTemplate.opsForHash().hasKey(Constants.MOVEMENTS_INTERACT_KEY,
                    Constants.MOVEMENT_ISLIKE_HASHKEY + UserThreadLocal.getUserId());
            if (!isLike) {
                // 从MongoDB中进行查询
                isLike = commentApi.queryUserIsLike(UserThreadLocal.getUserId(), comment.getId());
                if (isLike) {
                    // 把查询到数据给保存到Redis中
                    redisTemplate.opsForHash().put(Constants.MOVEMENTS_INTERACT_KEY + comment.getId(),
                            Constants.MOVEMENT_ISLIKE_HASHKEY + UserThreadLocal.getUserId(), "1");
                }
            }
            // 当前登录用户是否对该评论点赞
            commentVo.setHasLiked(isLike ? 1 : 0);
            voList.add(commentVo);
        }
        pageResult.setItems(voList);
        return pageResult;
    }

    /**
     * 发布评论
     * @param publishId
     * @param content
     */
    public void saveComments(String publishId, String content) {
        // 调用Dubbo实现一个评论
        Comment comment = new Comment();
        // 评论ID
        comment.setId(new ObjectId());
        // 圈子ID
        comment.setPublishId(new ObjectId(publishId));
        // 评论类型
        comment.setCommentType(CommentType.COMMENT_TYPE.getType());
        // 评论的文本
        comment.setContent(content);
        // 评论人的ID
        comment.setUserId(UserThreadLocal.getUserId());
        // 发布圈子的人的ID
        // 需要根据圈子的id去查询一个圈子详情 从中获取发布人的id
        Publish publish = publishApi.queryPublishById(publishId);
        comment.setPublishUserId(publish.getUserId());
        // 评论树 该功能不实现
        comment.setIsParent(false);
        comment.setParentId(null);
        comment.setCreated(System.currentTimeMillis());

        Long commentCount = commentApi.saveComment(comment);
        // 把当前这个圈子的点赞数给保存到redis中
        redisTemplate.opsForHash().put(Constants.MOVEMENTS_INTERACT_KEY + publishId,
                Constants.MOVEMENT_COMMENT_HASHKEY, commentCount.toString());

    }

    public Long publicLikeComment(String publishId) {
        // 调用Dubbo实现一个点赞
        Comment comment = new Comment();
        // 评论ID
        comment.setId(new ObjectId());
        // 圈子ID/评论ID
        comment.setPublishId(new ObjectId(publishId));
        // 评论类型
        comment.setCommentType(CommentType.LIKE_TYPE.getType());
        // 评论的文本
        comment.setContent(null);
        // 评论人的ID
        comment.setUserId(UserThreadLocal.getUserId());
        // 发布圈子的人的ID
        // 需要根据圈子的id去查询一个圈子详情 从中获取发布人的id
        Publish publish = publishApi.queryPublishById(publishId);
        // 说明当前传递过来的publishId不是圈子的ID
        if (ObjectUtil.isNotNull(publish)) {
            comment.setPublishUserId(publish.getUserId());
        } else {
            // publishId有可能是评论表中的ID 也有可能是 小视频中的ID
            // 根据这个publishId去查询评论表
            Comment commentId = commentApi.queryCommentById(publishId);
            if (ObjectUtil.isNotNull(commentId)){
                comment.setPublishUserId(commentId.getUserId());
            }else {
                Video video = videoApi.queryVideoById(publishId);
                comment.setPublishUserId(video.getUserId());
            }
        }

        // 评论树 该功能不实现
        comment.setIsParent(false);
        comment.setParentId(null);
        comment.setCreated(System.currentTimeMillis());

        Long likeCount = commentApi.likeComment(comment);
        return likeCount;
    }

    private Long publicLoveComment(String publishId) {
        // 调用Dubbo实现一个喜欢
        Comment comment = new Comment();
        // 评论ID
        comment.setId(new ObjectId());
        // 圈子ID/评论ID
        comment.setPublishId(new ObjectId(publishId));
        // 评论类型
        comment.setCommentType(CommentType.LOVE_TYPE.getType());
        // 评论的文本
        comment.setContent(null);
        // 评论人的ID
        comment.setUserId(UserThreadLocal.getUserId());
        // 发布圈子的人的ID
        // 需要根据圈子的id去查询一个圈子详情 从中获取发布人的id
        Publish publish = publishApi.queryPublishById(publishId);
        // 说明当前传递过来的publishId不是圈子的ID
        if (ObjectUtil.isNotNull(publish)) {
            comment.setPublishUserId(publish.getUserId());
        } else {
            // publishId有可能是评论表中的ID 也有可能是 小视频中的ID
            // 根据这个publishId去查询评论表
            Comment commentId = commentApi.queryCommentById(publishId);
            if (ObjectUtil.isNotNull(commentId)) {
                comment.setPublishUserId(commentId.getUserId());
            } else {
                Video video = videoApi.queryVideoById(publishId);
                comment.setPublishUserId(video.getUserId());
            }
        }

        // 评论树 该功能不实现
        comment.setIsParent(false);
        comment.setParentId(null);
        comment.setCreated(System.currentTimeMillis());

        Long loveCount = commentApi.loveComment(comment);
        return loveCount;
    }
}

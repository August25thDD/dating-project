package com.yang.dubbo.interfaces;

import com.alibaba.dubbo.config.annotation.Service;
import com.yang.commons.pojo.Comment;
import com.yang.commons.pojo.Publish;
import com.yang.commons.vo.PageResult;
import org.bson.types.ObjectId;


public interface CommentApi {

    /**
     * 圈子点赞
     * @param comment
     * @return
     */
    Long likeComment(Comment comment);

    /**
     * 取消点赞
     * @param userId
     * @param publishId
     * @return
     */
    Long unLikeComment(Long userId, ObjectId publishId);


    /**
     * 查询点赞数
     *
     * @param publishId
     * @return
     */
    Long queryLikeCount(ObjectId publishId);

    /**
     * 查询用户是否点赞该动态
     *
     * @param userId
     * @param publishId
     * @return
     */
    Boolean queryUserIsLike(Long userId, ObjectId publishId);


    /**
     * 喜欢
     * @param comment
     * @return
     */
    Long loveComment(Comment comment);

    /**
     * 取消喜欢
     * @param userId
     * @param publishId
     * @return
     */
    Long unLoveComment(Long userId, ObjectId publishId);


    /**
     * 查询喜欢数
     *
     * @param publishId
     * @return
     */
    Long queryLoveCount(ObjectId publishId);

    /**
     * 查询用户是否喜欢该动态
     *
     * @param userId
     * @param publishId
     * @return
     */
    Boolean queryUserIsLove(Long userId, ObjectId publishId);

    /**
     * 发表文本评论
     * @param comment
     * @return
     */
    Long saveComment(Comment comment);

    /**
     * 查询某个圈子的评论列表
     * @param publishId
     * @param page
     * @param pageSize
     * @return
     */
    PageResult queryCommentList(ObjectId publishId, Integer page, Integer pageSize);


    /**
     * 查询文本评论数
     * @param publishId
     * @return
     */
    Long queryCommentCount(ObjectId publishId);

    /**
     * 根据一个id查询一个评论对象
     * @param publishId
     * @return
     */
    Comment queryCommentById(String publishId);
}

package com.yang.dubbo.mongodb.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.dubbo.config.annotation.Service;
import com.yang.commons.enums.CommentType;
import com.yang.commons.pojo.Comment;
import com.yang.commons.pojo.Publish;
import com.yang.commons.vo.PageResult;
import com.yang.dubbo.interfaces.CommentApi;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @program: my-tanhua
 * @description:
 * @author: Mr.Yang
 * @create: 2021-12-09 19:05
 **/
@Service
public class CommentApiImpl implements CommentApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 点赞
     * @param comment
     * @return
     */
    @Override
    public Long likeComment(Comment comment) {
        mongoTemplate.save(comment);
        return queryLikeCount(comment.getPublishId());
    }

    /**
     * 取消点赞
     * @param userId
     * @param publishId
     * @return
     */
    @Override
    public Long disLikeComment(Long userId, ObjectId publishId) {
        mongoTemplate.remove(Query.query(Criteria.where("publishId").is(publishId)
                .and("userId").is(userId)
                .and("commentType").is(CommentType.LIKE_TYPE.getType())), Comment.class);
        return  queryLikeCount(publishId);
    }

    /**
     * 查询点赞数
     * @param publishId
     * @return
     */
    @Override
    public Long queryLikeCount(ObjectId publishId) {
        long count = mongoTemplate.count(Query.query(Criteria.where("publishId").is(publishId)
                .and("commentType").is(CommentType.LIKE_TYPE.getType())), Comment.class);
        return count;
    }

    /**
     * 查询喜欢数
     * @param publishId
     * @return
     */
    @Override
    public Long queryLoveCount(ObjectId publishId) {
        long count = mongoTemplate.count(Query.query(Criteria.where("publishId").is(publishId)
                .and("commentType").is(CommentType.LOVE_TYPE.getType())), Comment.class);
        return count;
    }

    /**
     * 查询评论数
     * @param publishId
     * @return
     */
    @Override
    public Long queryCommentCount(ObjectId publishId) {
        long count = mongoTemplate.count(Query.query(Criteria.where("publishId").is(publishId)
                .and("commentType").is(CommentType.COMMENT_TYPE.getType())), Comment.class);
        return count;
    }

    /**
     * 查询是否点赞
     * @param userId
     * @param publishId
     * @return
     */
    @Override
    public Boolean queryUserIsLike(Long userId, ObjectId publishId) {
        List<Comment> comments = mongoTemplate.find(Query.query(Criteria.where("publishId").is(publishId)
                .and("userId").is(userId).and("commentType").is(CommentType.LIKE_TYPE.getType())
        ), Comment.class);
        return CollUtil.isNotEmpty(comments);
    }

    /**
     * 喜欢
     * @param comment
     * @return
     */
    @Override
    public Long loveComment(Comment comment) {
        mongoTemplate.save(comment);
        return queryLoveCount(comment.getPublishId());
    }

    /**
     * 取消喜欢
     * @param userId
     * @param publishId
     * @return
     */
    @Override
    public Long unLoveComment(Long userId, ObjectId publishId) {
        mongoTemplate.remove(Query.query(Criteria.where("publishId").is(publishId)
                .and("userId").is(userId)
                .and("commentType").is(CommentType.LOVE_TYPE.getType())), Comment.class);
        return  queryLoveCount(publishId);
    }

    /**
     * 查询是否喜欢
     * @param userId
     * @param publishId
     * @return
     */
    @Override
    public Boolean queryUserIsLove(Long userId, ObjectId publishId) {
        List<Comment> comments = mongoTemplate.find(Query.query(Criteria.where("publishId").is(publishId)
                .and("userId").is(userId).and("commentType")
                .is(CommentType.LOVE_TYPE.getType())
        ), Comment.class);
        return CollUtil.isNotEmpty(comments);
    }

    /**
     * 发表一个文本评论
     * @param comment
     * @return
     */
    @Override
    public Long saveComment(Comment comment) {
        mongoTemplate.save(comment);
        return queryCommentCount(comment.getPublishId());
    }


    /**
     * 查询某个圈子的评论列表
     *
     * @param publishId
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public PageResult queryCommentList(ObjectId publishId, Integer page, Integer pageSize) {
        List<Comment> comments = mongoTemplate.find(Query.query(Criteria.where("publishId").is(publishId)
                .and("commentType").is(CommentType.COMMENT_TYPE.getType()))
                .with(Sort.by(Sort.Order.desc("created")))
                .with(PageRequest.of(page - 1, pageSize)), Comment.class);
        return new PageResult(page, pageSize, queryCommentCount(publishId), comments);
    }



    /**
     * 查询评论对象根据id
     * @param publishId
     * @return
     */
    @Override
    public Comment queryCommentById(String publishId) {
        Comment comment = mongoTemplate.findOne(Query
                .query(Criteria.where("id").is(publishId)), Comment.class);
        return comment;
    }

}

package com.yang.appserver.controller;

import com.yang.appserver.service.CommentsService;
import com.yang.commons.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @program: my-tanhua
 * @description:
 * @author: Mr.Yang
 * @create: 2021-12-09 21:13
 **/
@RestController
@RequestMapping("/comments")
public class CommentsController {

    @Autowired
    private CommentsService commentsService;

    /**
     * 查询评论列表
     * @param publishId 圈子id
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping
    public ResponseEntity<PageResult> queryCommentsList(@RequestParam("movementId") String publishId,
                                                        @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                        @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize) {
        PageResult pageResult = commentsService.queryCommentsList(publishId, page, pageSize);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 发表评论
     * @param param
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> saveComments(@RequestBody Map<String, String> param) {
        String publishId = param.get("movementId");
        String content = param.get("comment");
        commentsService.saveComments(publishId, content);
        return ResponseEntity.ok(null);
    }

    /**
     * 评论点赞
     * @param publishId   文本评论的ID
     * @return
     */
    @GetMapping("{id}/like")
    public ResponseEntity<Long> likeComment(@PathVariable("id") String publishId) {
        Long likeCount = commentsService.likeComment(publishId);
        return ResponseEntity.ok(likeCount);
    }

    /**
     * 评论取消点赞
     *
     * @param publishId
     * @return
     */
    @GetMapping("{id}/dislike")
    public ResponseEntity<Long> disLikeComment(@PathVariable("id") String publishId) {
        Long likeCount = commentsService.disLikeComment(publishId);
        return ResponseEntity.ok(likeCount);
    }


}

package com.yang.appserver.controller;

import com.yang.appserver.service.VideoService;
import com.yang.commons.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @项目名称: tanhua-53
 * @包名: com.itheima.appserver.controller
 * @作者: 李云飞
 * @日期: 2021-12-09
 */
@RestController
@RequestMapping("smallVideos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    /**
     * 发布小视频
     *
     * @param picFile   封面图片
     * @param videoFile 视频文件
     * @return
     */
    @PostMapping
    public ResponseEntity saveVideo(@RequestParam("videoThumbnail") MultipartFile picFile,
                                    @RequestParam("videoFile") MultipartFile videoFile) {
        this.videoService.saveVideo(picFile, videoFile);
        return ResponseEntity.ok(null);
    }

    /**
     * 查询小视频列表
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping
    public ResponseEntity queryVideoList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                         @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize) {
        PageResult pageResult = this.videoService.queryVideoList(page, pageSize);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 视频点赞
     *
     * @param videoId 视频id
     * @return
     */
    @PostMapping("/{id}/like")
    public ResponseEntity likeComment(@PathVariable("id") String videoId) {
        Long likeCount = this.videoService.likeComment(videoId);
        return ResponseEntity.ok(likeCount);
    }

    /**
     * 取消点赞
     *
     * @param videoId
     * @return
     */
    @PostMapping("/{id}/dislike")
    public ResponseEntity disLikeComment(@PathVariable("id") String videoId) {
        Long likeCount = this.videoService.disLikeComment(videoId);
        return ResponseEntity.ok(likeCount);
    }

    /**
     * 评论列表
     */
    @GetMapping("/{id}/comments")
    public ResponseEntity queryCommentsList(@PathVariable("id") String videoId,
                                            @RequestParam(value = "page", defaultValue = "1") Integer page,
                                            @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize) {
        PageResult pageResult = this.videoService.queryCommentList(videoId, page, pageSize);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 提交评论
     *
     * @param param
     * @param videoId
     * @return
     */
    @PostMapping("/{id}/comments")
    public ResponseEntity saveComments(@RequestBody Map<String, String> param,
                                       @PathVariable("id") String videoId) {
        String content = param.get("comment");
        this.videoService.saveComment(videoId, content);
        return ResponseEntity.ok(null);
    }

    /**
     * 评论点赞
     *
     * @param videoCommentId 视频中的评论id
     * @return
     */
    @PostMapping("/comments/{id}/like")
    public ResponseEntity commentsLikeComment(@PathVariable("id") String videoCommentId) {
        Long likeCount = this.videoService.likeComment(videoCommentId);
        return ResponseEntity.ok(likeCount);
    }

    /**
     * 评论取消点赞
     *
     * @param videoCommentId 视频中的评论id
     * @return
     */
    @PostMapping("/comments/{id}/dislike")
    public ResponseEntity disCommentsLikeComment(@PathVariable("id") String videoCommentId) {
        Long likeCount = this.videoService.disLikeComment(videoCommentId);
        return ResponseEntity.ok(likeCount);
    }

    /**
     * 视频用户关注
     */
    @PostMapping("/{id}/userFocus")
    public ResponseEntity saveUserFocusComments(@PathVariable("id") Long userId) {
        this.videoService.followUser(userId);
        return ResponseEntity.ok(null);
    }

    /**
     * 取消视频用户关注
     */
    @PostMapping("/{id}/userUnFocus")
    public ResponseEntity saveUserUnFocusComments(@PathVariable("id") Long userId) {
        this.videoService.disFollowUser(userId);
        return ResponseEntity.ok(null);
    }
}

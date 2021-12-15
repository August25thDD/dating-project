package com.yang.appserver.service;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Lists;
import com.yang.appserver.interceptor.UserThreadLocal;
import com.yang.commons.constants.Constants;
import com.yang.commons.pojo.Comment;
import com.yang.commons.pojo.FollowUser;
import com.yang.commons.pojo.UserInfo;
import com.yang.commons.vo.CommentVo;
import com.yang.commons.vo.PageResult;
import com.yang.commons.vo.VideoVo;
import org.bson.types.ObjectId;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.yang.appserver.exception.ErrorResult;
import com.yang.appserver.exception.MyException;
import com.yang.commons.pojo.Video;
import com.yang.dubbo.interfaces.CommentApi;
import com.yang.dubbo.interfaces.UserInfoApi;
import com.yang.dubbo.interfaces.VideoApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @program: my-tanhua
 * @description:
 * @author: Mr.Yang
 * @create: 2021-12-15 09:39
 **/
@Service
public class VideoService {
    @Reference
    private VideoApi videoApi;

    @Autowired
    protected FastFileStorageClient storageClient;

    @Autowired
    private FdfsWebServer fdfsWebServer;

    @Reference
    private UserInfoApi userInfoApi;

    @Reference
    private CommentApi commentApi;

    @Autowired
    private CommentsService commentsService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    /**
     * 小视频上传
     *
     * @param picFile   封面图片
     * @param videoFile 视频文件
     * @return
     */
    @Autowired
    private IdService idService;
    public void saveVideo(MultipartFile picFile, MultipartFile videoFile) {
        // 先判断小视频的文件是否为空 如果为空就直接抛出"一个小视频内容不能为空"的异常
        // 判断 小视频 和  封面图片的 格式
        // 判断 小视频 和  封面图片的 大小
        // 把图片 和 小视频 上传到FastDFS中
        // 先上传封面图片文件  这个地方我没有校验
        try {
            StorePath picPath = this.storageClient.uploadFile(picFile.getInputStream(),
                    picFile.getSize(),
                    StrUtil.subAfter(picFile.getOriginalFilename(), ".", true),
                    null);
            StorePath videoPath = this.storageClient.uploadFile(videoFile.getInputStream(),
                    videoFile.getSize(),
                    StrUtil.subAfter(videoFile.getOriginalFilename(), ".", true), null);

            Video video = new Video();
            // 主键ID
            video.setId(new ObjectId());
            // 用于大数据推荐的VID
            video.setVid(idService.createId("VIDEO"));
            // 上传小视频的人的ID
            video.setUserId(0L);
            // 小视频的配文
            video.setText("可为空，前端并没有传值");
            // 封面图片地址
            video.setPicUrl(fdfsWebServer.getWebServerUrl() + picPath.getFullPath());
            // 小视频地址
            video.setVideoUrl(fdfsWebServer.getWebServerUrl()+videoPath.getFullPath());
            // 发布时间
            video.setCreated(System.currentTimeMillis());
            // 调用Dubbo实现小视频的发布
            videoApi.saveVideo(video);
        } catch (Exception e) {
            throw new MyException(ErrorResult.smallVideoUploadError());
        }

    }

    /**
     * 小视频列表
     * @param page
     * @param pageSize
     * @return
     */
    public PageResult queryVideoList(Integer page, Integer pageSize) {
        // 从Redis中查询推荐的VID
        String vids = redisTemplate.opsForValue().get(Constants.VIDEOS_RECOMMEND + UserThreadLocal.getUserId());

        // 手动分页(逻辑分页)  推荐系统一般都是每隔几分钟执行一次运算
        List<Video> videos = Collections.emptyList();
        if (StrUtil.isBlank(vids)) {
            // 没有推荐数据 从MongoDB中随机抽查一些数据返回
            videoApi.randomVideos(pageSize);
        } else {
            // [124,188,87,145,176,93,69,161,40,140,193,88,117,182,122,199,4,141,103,160]
            List<String> vidList = StrUtil.split(vids, ",");
            // 手动分页
            // 计算开始角标  结束角标  结束角标是否>总的数据长度
            // 1页   0     5
            // 2页   5     10
            // 3页   10    15
            // 12条数据  11
//            开始下标
            int startIndex = (page - 1) * pageSize;
            //            结束下标
            int endIndex = Math.min(startIndex + pageSize, vidList.size());
            // 当前页要显示的小视频的VID
            List<String> vidIdList = CollUtil.sub(vidList, startIndex, endIndex);
            // 根据redis中的pid查询到的圈子详情
            videos = videoApi.findVideosByVids(Convert.toList(Long.class, vidIdList));
        }

        // 查询发布人的信息
        // 从上面的结果集中提取发布人的id
        List<Long> userIds = CollUtil.getFieldValues(videos, "userId", Long.class);
        // 根据上面的这个用户id集合去查询他们的详细信息
        Map<Long, UserInfo> userInfoMap = userInfoApi.findUserInfoListByUserIds(userIds);
        // 拼凑VO对象
        ArrayList<VideoVo> videosVoList = new ArrayList<>();
        SetOperations<String, String> set = redisTemplate.opsForSet();
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        for (Video v : videos) {
            VideoVo videoVo = new VideoVo();
            videoVo.setId(v.getId().toHexString());
            videoVo.setCover(v.getPicUrl());
            videoVo.setVideoUrl(v.getPicUrl());
            videoVo.setSignature("");

            UserInfo userInfo = userInfoMap.get(v.getUserId());
            videoVo.setNickname(userInfo.getNickName());
            videoVo.setUserId(userInfo.getUserId());
            videoVo.setAvatar(userInfo.getLogo());

            // 点赞数
            videoVo.setLikeCount(Convert.toInt(hash.get(Constants.VIDEOS_INTERACT_KEY + v.getId(), Constants.VIDEO_LIKE_HASHKEY),0));
            // 是否点赞
            videoVo.setHasLiked(hash.hasKey(Constants.VIDEOS_INTERACT_KEY + v.getId(),
                    Constants.VIDEO_ISLIKE_HASHKEY + UserThreadLocal.getUserId()) ? 1 : 0);
            // 是否关注
            videoVo.setHasFocus(set.isMember(Constants.VIDEO_FOLLOW_HASHKEY + v.getId(), userInfo.getUserId().toString()) ? 1 : 0);
            // 评论数
            videoVo.setCommentCount(Convert.toInt(redisTemplate.opsForHash().get(Constants.VIDEOS_INTERACT_KEY + v.getId(), Constants.VIDEO_COMMENT_HASHKEY), 0));
        }
        // 返回的VO对象
        PageResult pageResult = new PageResult(page, pageSize, 0L, videosVoList);
        return pageResult;
    }

    /**
     * 小视频点赞
     *
     * @param videoId
     * @return
     */
    public Long likeComment(String videoId) {
        // 往MongoDB中插入一条点赞数据
        Long likeCount = commentsService.publicLikeComment(videoId);

        // 把当前这个视频的点赞数给保存到redis中
        redisTemplate.opsForHash().put(Constants.VIDEOS_INTERACT_KEY + videoId, Constants.VIDEO_LIKE_HASHKEY, likeCount.toString());
        // 把自己对这个圈子点赞的标记存储到redis中
        redisTemplate.opsForHash().put(Constants.VIDEOS_INTERACT_KEY + videoId, Constants.VIDEO_ISLIKE_HASHKEY + UserThreadLocal.getUserId(), "1");

        return likeCount;
    }

    /**
     * 小视频取消点赞
     *
     * @param videoId
     * @return
     */
    public Long disLikeComment(String videoId) {
        Long disLikeCount = commentApi.disLikeComment(UserThreadLocal.getUserId(), new ObjectId(videoId));
        // 把当前这个圈子的点赞数给保存到redis中
        redisTemplate.opsForHash().put(Constants.VIDEOS_INTERACT_KEY + videoId, Constants.VIDEO_LIKE_HASHKEY, disLikeCount.toString());
        // 把自己对这个圈子点赞的标记存储到redis中
        redisTemplate.opsForHash().delete(Constants.VIDEOS_INTERACT_KEY + videoId, Constants.VIDEO_ISLIKE_HASHKEY + UserThreadLocal.getUserId());
        return disLikeCount;
    }

    /**
     * 小视频文本评论
     *
     * @param videoId
     * @param content
     * @return
     */
    public void saveComment(String videoId, String content) {
        // 调用Dubbo实现一个评论
        Comment comment = new Comment();
        // 评论ID
        comment.setId(new ObjectId());
        // 圈子ID
        comment.setPublishId(new ObjectId(videoId));
        // 评论类型
        comment.setCommentType(2);
        // 评论的文本
        comment.setContent(content);
        // 评论人的ID
        comment.setUserId(UserThreadLocal.getUserId());
        // 发布圈子的人的ID
        // 需要根据圈子的id去查询一个圈子详情 从中获取发布人的id
        Video video = videoApi.queryVideoById(videoId);
        if (ObjectUtil.isNotNull(video)) {
            comment.setPublishUserId(video.getUserId());
        } else {
            // publishId有可能是评论表中的ID 也有可能是 小视频中的ID
            // 根据这个publishId去查询评论表
            Comment commentId = commentApi.queryCommentById(videoId);
            comment.setPublishUserId(commentId.getUserId());
        }


        // 评论树 该功能不实现
        comment.setIsParent(false);
        comment.setParentId(null);
        comment.setCreated(System.currentTimeMillis());

        Long commentCount = commentApi.saveComment(comment);
        // 把当前这个圈子的点赞数给保存到redis中
        redisTemplate.opsForHash().put(Constants.VIDEOS_INTERACT_KEY + videoId, Constants.VIDEO_COMMENT_HASHKEY, commentCount.toString());
    }

    /**
     * 小视频评论列表
     *
     * @param videoId
     * @param page
     * @param pageSize
     * @return
     */
    public PageResult queryCommentList(String videoId, Integer page, Integer pageSize) {
        PageResult pageResult = commentApi.queryCommentList(new ObjectId(videoId), page, pageSize);
        List<?> comments = pageResult.getItems();
        if (CollUtil.isEmpty(comments)) {
            return pageResult;
        }
        // 从评论列表中提取发布评论人的ID
        List<Long> userIds = CollUtil.getFieldValues(comments, "userId", Long.class);
        // 根据这个发布人的id集合去查询tb_user_info
        Map<Long, UserInfo> userInfoList = userInfoApi.findUserInfoListByUserIds(new UserInfo(), userIds);

        List<CommentVo> voList = new ArrayList<>();
        for (Object item : comments) {
            Comment comment = (Comment) item;
            // 拼凑vo对象
            CommentVo commentVo = new CommentVo();
            // 评论的id
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
            Long likeCount = Convert.toLong(redisTemplate.opsForHash().get(Constants.VIDEOS_INTERACT_KEY + comment.getId(), Constants.VIDEO_LIKE_HASHKEY));
            // 说明当前这个圈子没有人点赞 或者有人点赞但是Redis中没值
            if (ObjectUtil.isNull(likeCount)) {
                // 从MongoDB中进行查询
                likeCount = commentApi.queryLikeCount(comment.getId());
                // 把查询到数据给保存到Redis中
                redisTemplate.opsForHash().put(Constants.VIDEOS_INTERACT_KEY + comment.getId(), Constants.VIDEO_LIKE_HASHKEY, likeCount.toString());
            }
            commentVo.setLikeCount(Convert.toInt(likeCount));


            // 是否点赞
            Boolean isLike = redisTemplate.opsForHash().hasKey(Constants.VIDEOS_INTERACT_KEY, Constants.VIDEO_ISLIKE_HASHKEY + UserThreadLocal.getUserId());
            if (!isLike) {
                // 从MongoDB中进行查询
                isLike = commentApi.queryUserIsLike(UserThreadLocal.getUserId(), comment.getId());
                if (isLike) {
                    // 把查询到数据给保存到Redis中
                    redisTemplate.opsForHash().put(Constants.VIDEOS_INTERACT_KEY + comment.getId(), Constants.VIDEO_ISLIKE_HASHKEY + UserThreadLocal.getUserId(), "1");
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
     * 关注用户
     *
     * @param userId
     * @return
     */
    public void followUser(Long userId) {
        FollowUser followUser = new FollowUser();
        followUser.setId(new ObjectId());
        followUser.setUserId(UserThreadLocal.getUserId());
        // 传递过来的userId是主播的ID
        followUser.setFollowUserId(userId);
        followUser.setCreated(System.currentTimeMillis());

        videoApi.followUser(followUser);

        // 把关注的人的id缓存到Redis中
        redisTemplate.opsForSet().add(Constants.VIDEO_FOLLOW_HASHKEY + UserThreadLocal.getUserId(), userId.toString());
    }

    /**
     * 取消关注
     *
     * @param userId
     * @return
     */
    public void disFollowUser(Long userId) {
        videoApi.disFollowUser(UserThreadLocal.getUserId(), userId);
        // 从redis中删除取消关注的人
        redisTemplate.opsForSet().remove(Constants.VIDEO_FOLLOW_HASHKEY + UserThreadLocal.getUserId(), userId.toString());
    }
}

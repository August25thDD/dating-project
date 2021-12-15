package com.yang.appserver.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.collect.Lists;
import com.yang.appserver.exception.ErrorResult;
import com.yang.appserver.exception.MyException;
import com.yang.appserver.interceptor.UserThreadLocal;
import com.yang.commons.constants.Constants;
import com.yang.commons.pojo.Publish;
import com.yang.commons.pojo.UserInfo;
import com.yang.commons.utils.RelativeDateFormat;
import com.yang.commons.utils.UploadPicUtil;
import com.yang.commons.vo.PageResult;
import com.yang.commons.vo.QuanZiVo;
import com.yang.commons.vo.VisitorsVo;
import com.yang.dubbo.interfaces.CommentApi;
import com.yang.dubbo.interfaces.PublishApi;
import com.yang.dubbo.interfaces.UserInfoApi;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * @program: my-tanhua
 * @description:
 * @author: Mr.Yang
 * @create: 2021-12-08 16:07
 **/

@Service
public class PublishService {
    @Reference
    private PublishApi publishApi;

    @Reference
    private UserInfoApi userInfoApi;

    @Autowired
    private IdService idService;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private TimeLineService timeLineService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Reference
    private CommentApi commentApi;

    /**
     * 发布朋友圈
     *
     * @param textContent
     * @param location
     * @param latitude
     * @param longitude
     * @param multipartFiles
     * @throws IOException
     */
    public void publishMovement(String textContent,
                                String location,
                                String latitude,
                                String longitude,
                                MultipartFile[] multipartFiles) throws IOException {
        //      处理文本
        //      判断文字是否为空
        if (StrUtil.isEmpty(textContent)) {
            throw new MyException(ErrorResult.contentError());
        }
        // 处理图片
        // 判断图片是否为空
        // 创建一个接受图片地址的数组
        ArrayList<String> medias = new ArrayList<>();
        if (ArrayUtil.isNotEmpty(multipartFiles)) {
            for (MultipartFile file : multipartFiles) {
                // 遍历拿到每个图片 进行上传 返回的是图片的地址
                String pictureUrl = UploadPicUtil.uploadPicture(file.getBytes(),
                        StrUtil.subAfter(file.getOriginalFilename(), ".", true));
//                medias.add(pictureUrl);
                medias.add("https://hztanhua.oss-cn-hangzhou.aliyuncs.com/images/2021/12/07/9b532552fb034033be46a12792dc6e9a.jpeg");
            }
        }

        Publish publish = new Publish();
        // 主键ID
        publish.setId(new ObjectId());
        // PID 代表唯一的一个圈子
        // 我们的大数据系统会根据每个人的行为偏好去推荐圈子数据
        // 大数据系统在进行推荐的时候 会需要一个唯一ID来代表圈子对象
        // 但是这个唯一ID要求必须是数值型的 Long Integer
        // 怎么生成一个唯一的ID ??
        // Redis  Zookeeper
        publish.setPid(idService.createId("PUBLISH"));
        // 发布人的ID 当前登录人的ID
        publish.setUserId(UserThreadLocal.getUserId());
        // 圈子的文本内容
        publish.setText(textContent);
        // 圈子的图片
        publish.setMedias(medias);
        // 对谁可见 这个功能并没有实现 所以  随便写
        publish.setSeeType(0);
        // 对谁可见 可见人的列表 没有实现
        publish.setSeeList(Lists.newArrayList());
        // 对谁不可见 不可见人的列表
        publish.setNotSeeList(Lists.newArrayList());
//        经度
        publish.setLongitude(longitude);
//        纬度
        publish.setLatitude(latitude);
        // 位置描述
        publish.setLocationName(location);
        // 发布时间
        publish.setCreated(System.currentTimeMillis());
        // 保存到发布表中 同时给我们返回刚刚保存的ID
        String publishId = publishApi.publish(publish);
        // 保存这个圈子的ID到自己的相册表中
        albumService.save(publishId);
        // 保存这个圈子ID到自己好友的时间线表中
        timeLineService.saveTimeLine(UserThreadLocal.getUserId(), publishId);
    }


    /**
     * 查询好友动态
     *
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult findFriendMovements(Integer page, Integer pagesize) {
        // 调用Dubbo分页查询圈子动态
        List<Publish> publishList = publishApi.findFriendMovements(page, pagesize, UserThreadLocal.getUserId());
        // 从上面的结果集中提取发布人的id
        List<Long> userIds = CollUtil.getFieldValues(publishList, "userId", Long.class);
        // 根据上面的这个用户id集合去查询他们的详细信息
        Map<Long, UserInfo> userInfoMap = userInfoApi.findUserInfoListByUserIds(userIds);
        if (CollUtil.isEmpty(userInfoMap)) {
            return new PageResult();
        }

        //调用自己学的封装类封装成前端需要的数据列表
        List<QuanZiVo> quanZiVoList = publishVODataFormart(publishList, userInfoMap);
        // 返回的VO对象

        PageResult pageResult = new PageResult(page, pagesize, 0L, quanZiVoList);
        return pageResult;
    }

    /**
     * 查询推荐动态
     *
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult findRecommendMovements(Integer page, Integer pagesize) {
        // 从Redis中查询推荐的PID
        String pids = redisTemplate.opsForValue().get(Constants.QUANZI_PUBLISH_RECOMMEND + UserThreadLocal.getUserId());
        // 手动分页(逻辑分页)  推荐系统一般都是每隔几分钟执行一次运算
        List<Publish> publishes = Collections.emptyList();
        if (StrUtil.isBlank(pids)) {
            // 没有推荐数据 从MongoDB中随机抽查一些数据返回
            publishes = publishApi.randomMovements(pagesize);
        } else {
            List<String> pidList = StrUtil.split(pids, ",");
            // 手动分页
            // 计算开始角标  结束角标  结束角标是否>总的数据长度
            // 1页   0     5
            // 2页   5     10
            // 3页   10    15
            // 12条数据  11
            int startIndex = (page - 1) * pagesize;
            int endIndex = Math.min(startIndex + pagesize, pidList.size());
            List<String> publishIdList = CollUtil.sub(pidList, startIndex, Math.min(startIndex + pagesize, pidList.size()));

            // 根据redis中的pid查询到的圈子详情
            publishes = publishApi.findMovementsByPids(Convert.toList(Long.class, publishIdList));
        }

        // 查询发布人的信息
        // 从上面的结果集中提取发布人的id
        List<Long> userIds = CollUtil.getFieldValues(publishes, "userId", Long.class);
        // 根据上面的这个用户id集合去查询他们的详细信息
        Map<Long, UserInfo> userInfoMap = userInfoApi.findUserInfoListByUserIds(userIds);
        // 拼凑VO对象
        List<QuanZiVo> quanZiVoList = publishVODataFormart(publishes, userInfoMap);

        // 返回的VO对象
        PageResult pageResult = new PageResult(page, pagesize, 0L, quanZiVoList);
        return pageResult;
    }

    private List<QuanZiVo> publishVODataFormart(List<Publish> publishList, Map<Long, UserInfo> userInfoMap) {
        List<QuanZiVo> quanZiVoList = new ArrayList<>();
        for (Publish publish : publishList) {
            QuanZiVo quanZiVo = new QuanZiVo();
            // 圈子ID
            quanZiVo.setId(publish.getId().toHexString());
            // 圈子发布人ID
            quanZiVo.setUserId(publish.getUserId());
            // 圈子文本内容
            quanZiVo.setTextContent(publish.getText());
            // 圈子的图片
            quanZiVo.setImageContent(Convert.toStrArray(publish.getMedias()));
            // 圈子的发布时间
            quanZiVo.setCreateDate(RelativeDateFormat.format(new Date(publish.getCreated())));

            // 获取发布人的信息
            UserInfo userInfo = userInfoMap.get(publish.getUserId());
            // 头像
            quanZiVo.setAvatar(userInfo.getLogo());
            // 昵称
            quanZiVo.setNickname(userInfo.getNickName());
            // 性别
            quanZiVo.setGender(userInfo.getSex() == 1 ? "man" : "woman");
            // 年龄
            quanZiVo.setAge(userInfo.getAge());
            // 标签
            quanZiVo.setTags(StrUtil.splitToArray(userInfo.getTags(), ","));

            // 评论相关
            // 先给一些默认值 等到 后台讲完评论
            Long likeCount = Convert.toLong(redisTemplate.opsForHash()
                    .get(Constants.MOVEMENTS_INTERACT_KEY + publish.getId()
                    , Constants.MOVEMENT_LIKE_HASHKEY));
//           说明这个圈子没有人点赞或者是有人点赞但是redis中没有值
            if (ObjectUtil.isNull(likeCount)) {
//                从MongoDB中进行查询
                 likeCount = commentApi.queryLikeCount(publish.getId());
                 redisTemplate.opsForHash().put(Constants.MOVEMENTS_INTERACT_KEY+publish.getId(),
                         Constants.MOVEMENT_LIKE_HASHKEY,likeCount.toString());
            }
            quanZiVo.setLikeCount(Convert.toInt(likeCount));

            // 评论数
            Long commentCount = Convert.toLong(redisTemplate.opsForHash()
                    .get(Constants.MOVEMENTS_INTERACT_KEY + publish.getId(), Constants.MOVEMENT_COMMENT_HASHKEY));
            if (ObjectUtil.isNull(commentCount)) {
                // 从MongoDB中进行查询
                commentCount = commentApi.queryCommentCount(publish.getId());
                // 把查询到数据给保存到Redis中
                redisTemplate.opsForHash().put(Constants.MOVEMENTS_INTERACT_KEY + publish.getId()
                        ,Constants.MOVEMENT_COMMENT_HASHKEY, commentCount.toString());
            }
            quanZiVo.setCommentCount(Convert.toInt(commentCount));

            // 喜欢数
            Long loveCount = Convert.toLong(redisTemplate.opsForHash()
                    .get(Constants.MOVEMENTS_INTERACT_KEY + publish.getId(), Constants.MOVEMENT_LOVE_HASHKEY));
            if (ObjectUtil.isNull(loveCount)) {
                // 从MongoDB中进行查询
                loveCount = commentApi.queryLoveCount(publish.getId());
                // 把查询到数据给保存到Redis中
                redisTemplate.opsForHash().put(Constants.MOVEMENTS_INTERACT_KEY + publish.getId()
                        ,Constants.MOVEMENT_LOVE_HASHKEY, loveCount.toString());
            }
            quanZiVo.setLoveCount(Convert.toInt(loveCount));


            // 是否点赞
            Boolean isLike = redisTemplate.opsForHash().hasKey(Constants.MOVEMENTS_INTERACT_KEY
                    , Constants.MOVEMENT_ISLIKE_HASHKEY + UserThreadLocal.getUserId());
            if (!isLike) {
                // 从MongoDB中进行查询
                isLike = commentApi.queryUserIsLike(UserThreadLocal.getUserId(), publish.getId());
                if (isLike) {
                    // 把查询到数据给保存到Redis中
                    redisTemplate.opsForHash().put(Constants.MOVEMENTS_INTERACT_KEY + publish.getId()
                            , Constants.MOVEMENT_ISLIKE_HASHKEY + UserThreadLocal.getUserId(), "1");
                }
            }
            quanZiVo.setHasLiked(isLike ? 1 : 0);

            // 是否喜欢
            Boolean isLove = redisTemplate.opsForHash().hasKey(Constants.MOVEMENTS_INTERACT_KEY
                    , Constants.MOVEMENT_ISLOVE_HASHKEY + UserThreadLocal.getUserId());
            if (!isLove) {
                // 从MongoDB中进行查询
                isLove = commentApi.queryUserIsLove(UserThreadLocal.getUserId(), publish.getId());
                if (isLove) {
                    // 把查询到数据给保存到Redis中
                    redisTemplate.opsForHash().put(Constants.MOVEMENTS_INTERACT_KEY + publish.getId()
                            , Constants.MOVEMENT_ISLOVE_HASHKEY + UserThreadLocal.getUserId(), "1");
                }
            }
            quanZiVo.setHasLoved(isLove ? 1 : 0);

            // 写死距离
            quanZiVo.setDistance("1.2公里");

            quanZiVoList.add(quanZiVo);
        }
        return quanZiVoList;
    }

    /**
     * 查询单条动态信息
     * @param publishId
     * @return
     */
    public QuanZiVo queryById(String publishId) {
        Publish publish = publishApi.queryPublishById(publishId);
        UserInfo userInfo = userInfoApi.findUserInfoByUserId(publish.getUserId());
        List<QuanZiVo> quanZiVoList = publishVODataFormart(CollUtil.toList(publish),
                MapUtil.builder(userInfo.getUserId(), userInfo).build());
        return quanZiVoList.get(0);
    }

    // 查询个人的所有圈子详情
    public PageResult queryAlbumList(Long userId, Integer page, Integer pageSize) {
        // 这个人发布过的所有圈子 分页 排序
        List<Publish> movements = publishApi.findMovementsByUserId(page, pageSize, userId);

        // 查询发布人的信息
        UserInfo userinfo = userInfoApi.findUserInfoByUserId(userId);
        //  拼凑VO对象
        List<QuanZiVo> quanZiVos = publishVODataFormart(movements, MapUtil.builder(userId, userinfo).build());
        return new PageResult(page, pageSize, 0L, quanZiVos);
    }

    public List<VisitorsVo> visitorsTop() {

        return null;
    }
}

package com.yang.commons.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoVo {
        // APP 返回了null  可能会报错 APP页面就不展示任何东西
    // 小视频的id
    private String id;
    // 小视频发布人的id
    private Long userId;
    // 发布人头像
    private String avatar;
    // 发布人昵称
    private String nickname;
    // 封面
    private String cover;
    // 视频URL
    private String videoUrl;
    // 签名
    private String signature;
    //点赞数量
    private Integer likeCount;
    //是否已赞（1是，0否）
    private Integer hasLiked;
    //是是否关注 （1是，0否）
    private Integer hasFocus;
    //评论数量
    private Integer commentCount;
}
package com.yang.commons.constants;

/**
 * @项目名称: tanhua-53
 * @包名: com.itheima.commons.constants
 * @作者: 李云飞
 * @日期: 2021-12-03
 * <p>
 * 常量类
 */
public class Constants {

    /**
     * 手机APP短信验证码CHECK_CODE_
     */
    public static final String SMS_CODE = "CHECK_CODE_";

    /**
     * 登录短信模板编码
     */
    public static final String SMS_CODE_TEMPLATE = "SMS_184110818";

    /**
     * JWT加密盐
     */
    public static final String JWT_SECRET = "itcast";

    /**
     * JWT超时时间(默认七天)
     */
    public static final int JWT_TIME_OUT = 1000 * 60 * 60 * 24 * 7;

    //推荐动态
    public static final String QUANZI_PUBLISH_RECOMMEND = "QUANZI_PUBLISH_RECOMMEND_";

    //推荐视频
    public static final String VIDEOS_RECOMMEND = "VIDEOS_RECOMMEND_";

    //圈子互动KEY
    public static final String MOVEMENTS_INTERACT_KEY = "MOVEMENTS_INTERACT_";

    //动态点赞用户HashKey
    public static final String MOVEMENT_LIKE_HASHKEY = "MOVEMENT_LIKE_";

    //动态喜欢用户HashKey
    public static final String MOVEMENT_LOVE_HASHKEY = "MOVEMENT_LOVE_";

    //视频点赞用户HashKey
    public static final String VIDEO_LIKE_HASHKEY = "VIDEO_LIKE";

    //访问用户
    public static final String VISITORS_USER = "VISITOR_USER";

    //初始化密码
    public static final String INIT_PASSWORD = "123456";

    //环信用户前缀
    public static final String HX_USER_PREFIX = "hx";

    //用户喜欢Redis的key
    public static final String USER_LIKE_KEY = "USER_LIKE_SET_";

    //用户不喜欢Redis的key
    public static final String USER_NOT_LIKE_KEY = "USER_NOT_LIKE_SET_";

    //关注用户的key
    public static final String FOCUS_USER_KEY = "FOCUS_USER_KEY_";

    // PID/VID在Redis中的Key
    public static final String TANHUA_UNIT_ID = "TANHUA_UNIT_ID";
}

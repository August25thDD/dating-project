package com.yang.commons.vo;

import cn.hutool.core.util.ObjectUtil;
import com.yang.commons.pojo.RecommendUser;
import com.yang.commons.pojo.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 今日佳人
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodayBest {

    private Long id; //用户id
    private String avatar;
    private String nickname;
    private String gender; //性别 man woman
    private Integer age;
    private String[] tags;
    private Long fateValue; //缘分值

    /**
     * 在vo对象中，补充一个工具方法，封装转化过程
     */
    public static TodayBest init(UserInfo userInfo, RecommendUser recommendUser) {
        if (ObjectUtil.isNull(userInfo)){
            return null;
        }
        TodayBest vo = new TodayBest();
        vo.setId(userInfo.getUserId());
        vo.setAvatar(userInfo.getLogo());
        vo.setNickname(userInfo.getNickName());
        vo.setGender(userInfo.getSex() == 1 ? "man" : "woman");
        vo.setAge(userInfo.getAge());
        vo.setFateValue(recommendUser.getScore().longValue());
        if (userInfo.getTags() != null) {
            vo.setTags(userInfo.getTags().split(","));
        }
        return vo;
    }
}
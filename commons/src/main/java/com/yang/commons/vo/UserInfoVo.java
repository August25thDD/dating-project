package com.yang.commons.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoVo implements Serializable {

    private Long id; //用户id
    private String avatar; //头像
    private String nickname; //昵称
    private String birthday; //生日 2019-09-11
    private String age; //年龄
    private String gender; //性别 man woman
    private String city; //城市
    private String education; //学历
    private String income; //月收入
    private String profession; //行业
    private Integer marriage; //婚姻状态（0未婚，1已婚）

}
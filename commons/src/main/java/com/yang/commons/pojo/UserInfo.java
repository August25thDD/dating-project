package com.yang.commons.pojo;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @项目名称: tanhua-53
 * @包名: com.itheima.dubbo.domian
 * @作者: 李云飞
 * @日期: 2021-12-03
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfo implements Serializable {

    private Long id;
    private Long userId; //用户id
    private String nickName; //昵称
    private String logo; //用户头像
    private String tags; //用户标签：多个用逗号分隔
    private Integer sex; //性别
    private Integer age; //年龄
    private String edu; //学历
    private String city; //城市
    private String birthday; //生日
    private String coverPic; // 封面图片
    private String industry; //行业
    private String income; //收入
    private String marriage; //婚姻状态
    private Date created; // 创建时间
    private Date updated; // 修改时间

    public void setSex(String sex) {
        if (StrUtil.isNotBlank(sex)) {
            this.sex = "man".equals(sex) ? 1 : 2;
        } else {
            this.sex = null;
        }
    }
}

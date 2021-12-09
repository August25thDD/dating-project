package com.yang.commons.pojo;

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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements Serializable {
    private Long id;
    private String mobile;
    private String password;
    private Date created;
    private Date updated;
}

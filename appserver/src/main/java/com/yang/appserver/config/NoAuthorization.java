package com.yang.appserver.config;

import java.lang.annotation.*;

/**
 * @项目名称: tanhua-53
 * @包名: com.itheima.appserver.interceptor
 * @作者: 李云飞
 * @日期: 2021-12-06
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoAuthorization {
}
